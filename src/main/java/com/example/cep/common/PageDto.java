package com.example.cep.common;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class PageDto {
  int page;

  int size;

  boolean isAsc;

  String sortBy;

  public Pageable toPageable() {
    if(Objects.nonNull(sortBy)){
      return isAsc ? PageRequest.of(page, size,
          Sort.by(sortBy).descending()) : PageRequest.of(page, size,
          Sort.by(sortBy).ascending());
    }
    return isAsc ? PageRequest.of(page, size,
        Sort.by("createdAt").descending()) : PageRequest.of(page, size,
        Sort.by("createdAt").ascending());
  }

}
