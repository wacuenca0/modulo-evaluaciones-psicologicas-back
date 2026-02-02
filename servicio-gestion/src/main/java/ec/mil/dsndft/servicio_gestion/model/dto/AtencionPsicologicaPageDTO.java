package ec.mil.dsndft.servicio_gestion.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AtencionPsicologicaPageDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("content")
    private List<AtencionPsicologicaResponseDTO> content;
    @JsonProperty("page")
    private int page;
    @JsonProperty("size")
    private int size;
    @JsonProperty("totalElements")
    private long totalElements;
    @JsonProperty("totalPages")
    private int totalPages;
    @JsonProperty("last")
    private boolean last;
    @JsonProperty("first")
    private boolean first;

    public AtencionPsicologicaPageDTO() {}

    public AtencionPsicologicaPageDTO(List<AtencionPsicologicaResponseDTO> content, int page, int size, long totalElements, int totalPages, boolean last, boolean first) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
        this.first = first;
    }

    public List<AtencionPsicologicaResponseDTO> getContent() {
        return content;
    }
    public void setContent(List<AtencionPsicologicaResponseDTO> content) {
        this.content = content;
    }
    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public long getTotalElements() {
        return totalElements;
    }
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
    public int getTotalPages() {
        return totalPages;
    }
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    public boolean isLast() {
        return last;
    }
    public void setLast(boolean last) {
        this.last = last;
    }
    public boolean isFirst() {
        return first;
    }
    public void setFirst(boolean first) {
        this.first = first;
    }
}
