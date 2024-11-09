package cn.octopus.paperplagiarism.util;

import cn.hutool.core.io.FileTypeUtil;
import com.huaban.analysis.jieba.JiebaSegmenter;
import lombok.Data;

import java.io.File;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class SimHash {

    // 原始句子
    private String sentence;
    // 生成的SimHash指纹
    private BigInteger strSimHash;
    // 哈希位数
    private int hashBits = 128;
    // 用于分割句子的标点符号
    private static final String[] cutLineFlag = {"？", "！", "。", "…", "】"};

    /**
     * 构造函数，使用默认哈希位数
     *
     * @param sentence 输入的文本
     */
    public SimHash(String sentence) {
        this.sentence = sentence;
        this.strSimHash = this.simHash();
    }

    /**
     * 生成SimHash指纹
     *
     * @return 生成的SimHash指纹
     */
    public BigInteger simHash() {
        // 初始化哈希向量
        int[] v = new int[this.hashBits];
        // 将句子分词
        List<String> words = cutSentenceToWords(sentence);

        // 对每个词进行哈希并更新向量
        for (String word : words) {
            BigInteger t = this.hash(word);
            for (int i = 0; i < this.hashBits; i++) {
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                if (t.and(bitmask).signum() != 0) {
                    v[i] += 1;
                } else {
                    v[i] -= 1;
                }
            }
        }

        // 生成最终的指纹
        BigInteger fingerprint = new BigInteger("0");
        for (int i = 0; i < this.hashBits; i++) {
            if (v[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
            }
        }
        return fingerprint;
    }

    // 将句子分词
    private static List<String> cutSentenceToWords(String sentence) {
        JiebaSegmenter segmenter = new JiebaSegmenter();
        return segmenter.process(sentence, JiebaSegmenter.SegMode.SEARCH).stream()
                .map(e -> e.word).collect(Collectors.toList());
    }

    /**
     * 对单个字符串进行哈希
     *
     * @param source 输入的字符串
     * @return 哈希后的结果
     */
    private BigInteger hash(String source) {
        // 空字符串或null返回0
        if (source == null || source.length() == 0) {
            return new BigInteger("0");
        }

        // 初始化变量
        char[] sourceArray = source.toCharArray();
        BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
        BigInteger m = new BigInteger("1000003");
        BigInteger mask = new BigInteger("2").pow(this.hashBits).subtract(new BigInteger("1"));

        // 遍历字符串进行哈希计算
        for (char item : sourceArray) {
            BigInteger temp = BigInteger.valueOf(item);
            x = x.multiply(m).xor(temp).and(mask);
        }

        // 加上字符串长度作为最后一步哈希
        x = x.xor(new BigInteger(String.valueOf(source.length())));

        // 处理特殊情况
        if (x.equals(new BigInteger("-1"))) {
            x = new BigInteger("-2");
        }
        return x;
    }

    /**
     * 计算当前SimHash对象与另一个SimHash对象之间的汉明距离。
     * 汉明距离是两个字符串对应位置的不同字符的个数，在这里指两个二进制数不同位的数量。
     *
     * @param simHash 另一个SimHash对象
     * @return 当前对象与other对象之间的汉明距离
     */
    public int hammingDistance(SimHash simHash) {
        // 创建一个掩码，它是一个全1的BigInteger，位数与hashBits相同，用于按位比较
        BigInteger m = new BigInteger("1").shiftLeft(this.hashBits).subtract(new BigInteger("1"));

        // 对两个SimHash对象的strSimHash进行异或操作，得到结果x
        // 这一步是为了找到两个二进制数中不同的位
        BigInteger x = this.strSimHash.xor(simHash.strSimHash).and(m);

        int tot = 0; // 用于记录汉明距离，即不同位的数量

        // 当x不为0时，循环继续
        // 每一次循环都找到x的最低位的1（即最右边的1），然后将其置为0
        // 这样就能统计出x中1的个数，即两个SimHash之间不同位的数量
        while (x.signum() != 0) {
            tot += 1; // 发现一个不同位，增加计数器
            x = x.and(x.subtract(new BigInteger("1"))); // 将x的最低位的1置为0
        }

        return tot; // 返回计算得到的汉明距离
    }

    /**
     * 将每篇文章分句
     *
     * @param text
     * @return
     */
    public static List<String> cutTextToSentences(String text) {
        // 使用正则表达式分割句子，考虑多种标点符号
        String[] sentences = text.trim().split("[。？…！；：\\n\\r]+");

        // 使用Java 8的流来过滤和收集非空且长度大于4的句子
        return Arrays.stream(sentences)
                .map(String::trim)
                .filter(sentence -> sentence.length() > 4)
                .collect(Collectors.toList());
    }

    /**
     * 将每篇文章分句
     *
     * @param text
     * @return
     */
    public static List<String> cutTextToLines(String text) {
        // 使用正则表达式分割句子，考虑多种标点符号
        String[] lines = text.trim().split("[\\n]+");

        // 使用Java 8的流来过滤和收集非空且长度大于4的句子
        return Arrays.stream(lines)
                .map(String::trim)
                .filter(sentence -> sentence.length() > 4)
                .collect(Collectors.toList());
    }

    public static Map<File, List<SimHash>> getSimHashesDoc(String folderFiles) {
        Map<File, List<SimHash>> simHashesDoc = new HashMap<>();
        File[] folderFiless = FileCompareUtil.getValidFiles(folderFiles);
        for (File file : folderFiless) {
            simHashesDoc.put(file, generateSimHashes(file));
        }
        return simHashesDoc;
    }

    public static List<SimHash> generateSimHashes(File file) {
        String content = readFileContent(file);
        List<String> sentences = SimHash.cutTextToSentences(content);
        List<SimHash> simHashes = new ArrayList<>();
        for (String sentence : sentences) {
            simHashes.add(new SimHash(sentence));
        }
        return simHashes;
    }

    // 定义一个方法，根据文件扩展名读取文件内容
    private static String readFileContent(File file) {// 确保扩展名为小写
        String content = "";
        String fileType = FileTypeUtil.getType(file);
        if ("doc".equals(fileType) || "docx".equals(fileType) || "wps".equals(fileType)) {
            content = WordUtil.readWord(file);
        } else if ("pdf".equals(fileType)) {
            content = PdfUtil.readPdf(file);
        } else if ("rtf".equals(fileType)) {
            content = RtfUtil.readRtf(file);
        }
        return content;
    }
}
