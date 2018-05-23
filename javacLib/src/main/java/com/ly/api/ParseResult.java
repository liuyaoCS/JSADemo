package com.ly.api;

import com.ly.sa.utils.ASTUtils;

/**
 * Created by liuyao-s on 2018/5/21.
 */

public class ParseResult {
    public ASTUtils.Type type = ASTUtils.Type.PASS;
    public int before = -1;
    public int after = -1;
}
