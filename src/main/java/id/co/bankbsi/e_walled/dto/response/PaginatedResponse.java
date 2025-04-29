package id.co.bankbsi.e_walled.dto.response;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class PaginatedResponse<T> {

    private List<T> data;
    private Meta meta;

    public PaginatedResponse(Page<T> page) {
        this.data = page.getContent();;
        this.meta = new Meta(page);;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Meta {
        private int currentPage;
        private int pageSize;
        private long totalItems;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;
        private boolean isFirst;
        private boolean isLast;

        public Meta(Page<?> page) {
            this.currentPage = page.getNumber() + 1; // Convert from 0-based to 1-based
            this.pageSize = page.getSize();
            this.totalItems = page.getTotalElements();
            this.totalPages = page.getTotalPages();
            this.hasNext = page.hasNext();
            this.hasPrevious = page.hasPrevious();
            this.isFirst = page.isFirst();
            this.isLast = page.isLast();
        }

    }

}
