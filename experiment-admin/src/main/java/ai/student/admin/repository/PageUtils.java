package ai.student.admin.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageUtils {
    public static PageRequest of(int start, int end, String sort, String order) {
        int size = end - start;
        int pageNumber = start  / size;
        return PageRequest.of(pageNumber, size, Sort.Direction.valueOf(order), sort);
    }
}
