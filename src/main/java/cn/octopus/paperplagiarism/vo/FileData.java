package cn.octopus.paperplagiarism.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FileData implements Serializable {
    private String fileName1;
    private String filePath1;
    private String fileName2;
    private String filePath2;
    private String percent;
}
