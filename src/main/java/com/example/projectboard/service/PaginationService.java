package com.example.projectboard.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class PaginationService {

    private static final int Bar_Length = 5;
    public List<Integer> getPaginationBarNumbers(int currentPageNumber, int totalPages){
        int startNumber = Math.max(currentPageNumber-(Bar_Length/2),0);
        int endNumber = Math.min(totalPages, startNumber+Bar_Length);
        return IntStream.range(startNumber,endNumber).boxed().toList();
    }

        public int currentBarLength() {
        return Bar_Length;
    }
}
