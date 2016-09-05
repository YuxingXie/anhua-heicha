package com.lingyun.common.web.tiles;

import org.apache.tiles.AttributeContext;
import org.apache.tiles.preparer.ViewPreparer;
import org.apache.tiles.request.Request;

/**
 * Created by Administrator on 2015/11/18.
 */
public class Prepare implements ViewPreparer{

    @Override
    public void execute(Request tilesContext, AttributeContext attributeContext) {
//        System.out.println(attributeContext.getAttribute("role"));
//        System.out.println(attributeContext.getAttribute("name"));
//        System.out.println("viewPreparer executed");
    }
}
