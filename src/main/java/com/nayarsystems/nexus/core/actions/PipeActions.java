package com.nayarsystems.nexus.core.actions;

import com.nayarsystems.nexus.core.components.Pipe;

import java.util.Map;
import java.util.function.Consumer;

public interface PipeActions {
    Pipe pipeOpen(String id);
    void pipeCreate(Integer length, Consumer<Pipe> cb);
}
