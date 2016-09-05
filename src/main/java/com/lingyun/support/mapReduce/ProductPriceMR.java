package com.lingyun.support.mapReduce;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;

/**
 * Created by Administrator on 2015/12/25.
 */
@Document(collection = "MapReduceProductPrice")
public class ProductPriceMR{
        public static String inputCollectionName="productSeries";
        public static String mapFun="function(){" +
                "var emitVal={}; " +
                "for(var i in this.productSeriesPrices){" +
                "emitVal.price=this.productSeriesPrices[i].price;" +
                "emitVal.productSeries=this;  " +
                "emitVal.productSeriesPrice=this.productSeriesPrices[i];" +
                "emit(this._id,emitVal);}}";
        public static String reduceFun="function(key,emits){}";
        public static MapReduceOptions mapReduceOptions(){
                MapReduceOptions mrO= new MapReduceOptions();
                mrO.outputCollection("MapReduceProductPrice");
                return mrO;
        }

        @Id
        private String id;
        @Field
        private ProductPriceMRValue value;

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public ProductPriceMRValue getValue() {
                return value;
        }

        public void setValue(ProductPriceMRValue value) {
                this.value = value;
        }
}
