package com.lingyun.mall.service.impl;
    import com.lingyun.common.constant.Constant;
    import com.lingyun.common.util.DateUtil;
    import com.lingyun.common.util.ExcelUtil;
    import com.lingyun.common.util.MD5;
    import com.lingyun.entity.*;
    import com.lingyun.mall.service.IHuanxunSupportBankService;
    import com.lingyun.mall.service.IHuanxunSupportOpeningBankService;
    import org.apache.logging.log4j.LogManager;
    import org.apache.logging.log4j.Logger;
    import org.springframework.stereotype.Service;

    import javax.annotation.Resource;
    import java.io.File;
    import java.io.FileNotFoundException;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.List;

@Service
public class StartOnLoadService {
    private static Logger logger = LogManager.getLogger();
    @Resource private IHuanxunSupportBankService huanxunSupportBankService;
    @Resource private IHuanxunSupportOpeningBankService huanxunSupportOpeningBankService;
    /**
     * Spring 容器初始化时加载
     */
    public void loadData() throws IOException {
        logger.info("容器初始化完成，载入初始化数据。。。。。。");
        File file = new File("D:\\develop\\projects\\ideaProjects\\anhua-heicha\\src\\main\\webapp\\statics\\template\\huanxun\\下载收款银行名称列表（新）.xls");
        String[][] result = ExcelUtil.getData(file, 1);
        String[] bankNames=new String[result.length];
        int rowLength = result.length;
        List<HuanxunSupportBank> banks=new ArrayList<HuanxunSupportBank>();
        for(int i=0;i<rowLength;i++) {
            HuanxunSupportBank bank=new HuanxunSupportBank();
            bank.setBankName(result[i][1]);
            banks.add(bank);
        }
        if (banks.size()>0) {
            huanxunSupportBankService.removeAll();
            huanxunSupportBankService.insertAll(banks);
        }
        File opbfile = new File("D:\\develop\\projects\\ideaProjects\\anhua-heicha\\src\\main\\webapp\\statics\\template\\huanxun\\开户行支行信息.xls");
        String[][] opbresult = ExcelUtil.getData(opbfile, 1);
        List<HuanxunSupportOpeningBank> openingBanks=new ArrayList<HuanxunSupportOpeningBank>();
        for (int i=0;i<opbresult.length;i++){
            HuanxunSupportOpeningBank openingBank=new HuanxunSupportOpeningBank();
            openingBank.setBankName(opbresult[i][1]);
            openingBanks.add(openingBank);
        }
        if (openingBanks.size()>0){
            huanxunSupportOpeningBankService.removeAll();
            huanxunSupportOpeningBankService.insertAll(openingBanks);
        }
    }


}