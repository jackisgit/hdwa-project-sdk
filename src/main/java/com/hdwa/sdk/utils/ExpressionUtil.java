package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.exception.ExceptionItem;
import com.hdwa.sdk.entity.expression.AdvancedExpressionLexer;
import com.hdwa.sdk.entity.expression.AdvancedExpressionParser;
import com.hdwa.sdk.entity.expression.AdvancedExpressionScanner;
import com.hdwa.sdk.entity.expression.AdvancedExpressionWalker;
import com.hdwa.sdk.entity.repository.RepositoryBase;
import com.hdwa.sdk.entity.repository.WalkerList;
import com.hdwa.sdk.entity.repository.WalkerWrapper;
import com.hdwa.sdk.entity.scene.DataProperty;
import lombok.extern.slf4j.Slf4j;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author abao
 * @since 2023/8/16
 * 异常处理工具类
 */
@Slf4j
public class ExpressionUtil {

    public static List<ExceptionItem> buildAndPut(RepositoryBase Repository, DataProperty sp, String expression, JSONObject CriteriaObject)
            throws Exception {
        List<ExceptionItem> exceptionList = new CopyOnWriteArrayList<ExceptionItem>();
        ANTLRInputStream input = new ANTLRInputStream(new ByteArrayInputStream((expression + "$").getBytes()));
        AdvancedExpressionLexer lexer = new AdvancedExpressionLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        AdvancedExpressionParser parser = new AdvancedExpressionParser(tokens);
        AdvancedExpressionParser.prog_return r = parser.prog();
        CommonTree t = (CommonTree) r.getTree();
        CommonTreeNodeStream nodes = new CommonTreeNodeStream(t);
        nodes.setTokenStream(tokens);

        AdvancedExpressionScanner scanner = new AdvancedExpressionScanner(nodes);
        scanner.prog();
        for (String var : scanner.varDict.keySet()) {
            if (!CriteriaObject.containsKey(var)) {
                ExceptionItem MyException = new ExceptionItem(PathUtil.getPropertyPath(Repository, sp), "var: " + var + " not exist in Criteria",
                        null);
                exceptionList.add(MyException);
            }
        }
        for (String varString : scanner.varStringDict.keySet()) {
            if (!CriteriaObject.containsKey(varString)) {
                ExceptionItem MyException = new ExceptionItem(PathUtil.getPropertyPath(Repository, sp),
                        "varString: " + varString + " not exist in Criteria", null);
                exceptionList.add(MyException);
            }
        }
        WalkerWrapper WalkerWrapper = new WalkerWrapper();
        WalkerWrapper.walker = new AdvancedExpressionWalker(nodes);
        WalkerWrapper.lock = new ReentrantLock(true);
        WalkerList WalkerList = new WalkerList(expression, 16);

        Repository.p2varDict.put(sp, scanner.varDict);
        Repository.p2varStringDict.put(sp, scanner.varStringDict);
        Repository.p2walker1.put(sp, WalkerWrapper);
        Repository.p2walker2.put(sp, WalkerList);

        return exceptionList;
    }
}
