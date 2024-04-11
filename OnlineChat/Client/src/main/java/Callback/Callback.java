package Callback;

import DTO.DTO;

@FunctionalInterface
public interface Callback {
    void call(DTO dto);
}