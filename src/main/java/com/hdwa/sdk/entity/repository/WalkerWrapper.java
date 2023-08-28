package com.hdwa.sdk.entity.repository;


import com.hdwa.sdk.expression.AdvancedExpressionWalker;

import java.util.concurrent.locks.ReentrantLock;

public class WalkerWrapper {
    public AdvancedExpressionWalker walker;
    public ReentrantLock lock;
}
