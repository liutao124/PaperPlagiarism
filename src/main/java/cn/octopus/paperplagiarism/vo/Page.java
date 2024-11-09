package cn.octopus.paperplagiarism.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class Page implements Serializable {
    private int curPage = 1;
    private int total = 0;
    private int pages = 1;
    private List<FileData> resultData = new ArrayList<>();
}
