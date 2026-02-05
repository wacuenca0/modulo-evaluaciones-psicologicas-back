package ec.mil.dsndft.servicio_gestion.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class AtencionPsicologicaPageSafeDTO {
    @JsonProperty("content")
    @JsonInclude(JsonInclude.Include.ALWAYS)
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

    public AtencionPsicologicaPageSafeDTO(List<AtencionPsicologicaResponseDTO> content, int page, int size, long totalElements, int totalPages, boolean last, boolean first) {
        this.content = (content == null) ? java.util.Collections.emptyList() : content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
        this.first = first;
    }

    public List<AtencionPsicologicaResponseDTO> getContent() {
        return (content == null) ? java.util.Collections.emptyList() : content;
    }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public boolean isLast() { return last; }
    public boolean isFirst() { return first; }
}