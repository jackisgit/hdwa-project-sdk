package com.hdwa.sdk.entity.repository;

import com.hdwa.sdk.expression.AdvancedExpressionLexer;
import com.hdwa.sdk.expression.AdvancedExpressionParser;
import com.hdwa.sdk.expression.AdvancedExpressionWalker;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

// 已废弃，改用WalkerWrapper
public class WalkerList {
    public int index = -1;
    public List<WalkerWrapper> wrapperList = new CopyOnWriteArrayList<WalkerWrapper>();

    public WalkerList(String expression, int count) throws Exception {
        for (int i = 0; i < count; i++) {
            WalkerWrapper WalkerWrapper = new WalkerWrapper();
            ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream((expression + "$").getBytes()));
            AdvancedExpressionLexer lexer = new AdvancedExpressionLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            AdvancedExpressionParser parser = new AdvancedExpressionParser(tokens);
            AdvancedExpressionParser.prog_return r = parser.prog();
            CommonTree t = (CommonTree) r.getTree();
            CommonTreeNodeStream nodes = new CommonTreeNodeStream(t);
            nodes.setTokenStream(tokens);

            WalkerWrapper.walker = new AdvancedExpressionWalker(nodes);
            WalkerWrapper.lock = new ReentrantLock(true);
            this.wrapperList.add(WalkerWrapper);
        }
    }

    public synchronized WalkerWrapper get() {
        index++;
        index = index % wrapperList.size();
        WalkerWrapper WalkerWrapper = this.wrapperList.get(index);
        return WalkerWrapper;
    }
}
