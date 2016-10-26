package com.lingyun.common.helper.service;
import com.lingyun.mall.service.*;
import com.lingyun.mall.service.impl.INotifyService;
import com.lingyun.mall.service.impl.IProductEvaluateService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
@Component
public class ServiceManager implements InitializingBean{

    private static boolean inited;
    public static IProductSeriesService productSeriesService;
    public static IProductPropertyService productPropertyService;
    public static ICartService cartService;
    public static IProductSubCategoryService productSubCategoryService;
    public static IProductPropertyValueService productPropertyValueService;
    public static IOrderService orderService;
    public static IBankService bankService;
    public static IFriendshipMallService friendshipMallService;
    public static IFriendshipExchangeService friendshipExchangeService;
    public static IAlipayBatchTransService alipayBatchTransService;
    public static IUserService userService;
    public static IAccountService accountService;

    public static IProductCategoryService productCategoryService;
//    public static IProductSeriesPriceService productSeriesPriceService;
//    public static IProductStoreInAndOutService productStoreInAndOutService;
    public static IInterestService interestService;
    public static IProductEvaluateService productEvaluateService;
    public static INotifyService notifyService;
    public static IHomePageBlockService homePageBlockService;
    public static IAdministratorService administratorService;
    public static ITopCarouselService topCarouselService;
    public static ISalesCampaignService salesCampaignService;
    public static IAuthorizeInfoService authorizeInfoService;
    public static IUserPointsService userPointsService;
    public static IUserMeasureService userMeasureService;
    public static IAlipayTransService alipayTransService;

    public void setAlipayBatchTransService(IAlipayBatchTransService alipayBatchTransService) {
        ServiceManager.alipayBatchTransService = alipayBatchTransService;
    }

    public void setBankService(IBankService bankService) {
        ServiceManager.bankService = bankService;
    }

    public void setUserPointsService(IUserPointsService userPointsService) {
        ServiceManager.userPointsService = userPointsService;
    }

    public void setUserMeasureService(IUserMeasureService userMeasureService) {
        ServiceManager.userMeasureService = userMeasureService;
    }

    public void setSalesCampaignService(ISalesCampaignService salesCampaignService) {
        ServiceManager.salesCampaignService = salesCampaignService;
    }

    public void setAlipayTransService(IAlipayTransService alipayTransService) {
        ServiceManager.alipayTransService = alipayTransService;
    }

    public void setTopCarouselService(ITopCarouselService topCarouselService) {
        ServiceManager.topCarouselService = topCarouselService;
    }

    public void setFriendshipExchangeService(IFriendshipExchangeService friendshipExchangeService) {
        ServiceManager.friendshipExchangeService = friendshipExchangeService;
    }

    public void setProductEvaluateService(IProductEvaluateService productEvaluateService) {
        ServiceManager.productEvaluateService = productEvaluateService;
    }

    public void setFriendshipMallService(IFriendshipMallService friendshipMallService) {
        ServiceManager.friendshipMallService = friendshipMallService;
    }

    public void setAdministratorService(IAdministratorService administratorService) {
        ServiceManager.administratorService = administratorService;
    }

    public void setHomePageBlockService(IHomePageBlockService homePageBlockService) {
        ServiceManager.homePageBlockService = homePageBlockService;
    }

    public void setNotifyService(INotifyService notifyService) {
        ServiceManager.notifyService = notifyService;
    }

//    public void setProductSeriesPriceService(IProductSeriesPriceService productSeriesPriceService) {
//        ServiceManager.productSeriesPriceService = productSeriesPriceService;
//    }

    public void setInterestService(IInterestService interestService) {
        ServiceManager.interestService = interestService;
    }

//    public void setProductStoreInAndOutService(IProductStoreInAndOutService productStoreInAndOutService) {
//        ServiceManager.productStoreInAndOutService = productStoreInAndOutService;
//    }



    public void setProductCategoryService(IProductCategoryService productCategoryService) {
        ServiceManager.productCategoryService = productCategoryService;
    }

    public static void setAuthorizeInfoService(IAuthorizeInfoService authorizeInfoService) {
        ServiceManager.authorizeInfoService = authorizeInfoService;
    }

    public void setAccountService(IAccountService accountService) {
        ServiceManager.accountService = accountService;
    }

    public void setOrderService(IOrderService orderService) {
        ServiceManager.orderService = orderService;
    }

    public static void setInited(boolean inited) {
        ServiceManager.inited = inited;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        inited = true;
    }
    public void setUserService(IUserService userService) {
        ServiceManager.userService = userService;
    }

    public void setProductSeriesService(IProductSeriesService productSeriesService) {
        ServiceManager.productSeriesService = productSeriesService;
    }

    public void setProductPropertyService(IProductPropertyService productPropertyService) {
        ServiceManager.productPropertyService = productPropertyService;
    }

    public void setCartService(ICartService cartService) {
        ServiceManager.cartService = cartService;
    }

    public void setProductSubCategoryService(IProductSubCategoryService productSubCategoryService) {
        ServiceManager.productSubCategoryService = productSubCategoryService;
    }

    public void setProductPropertyValueService(IProductPropertyValueService productPropertyValueService) {
        ServiceManager.productPropertyValueService = productPropertyValueService;
    }
}
