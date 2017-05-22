package com.lingyun.entity.field;

/**
 * Created by Administrator on 2017/5/19.
 */
public class BankAccount {
    /*
    收款户名|身份证号|手机号|收款银行|收款账号省份|收款账号地市|收款账号开户行|收款账号
    张三|320921198907235038|15201921718|中国工商银行|上海|上海|中国工商银行上海市天钥桥路支行|6222021207020987657
     */
    private String accountName;
    private String idCardNo;
    private String phone;
    private String bankName;
    private String province;
    private String city;
    private String openingBank;
    private String accountNo;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOpeningBank() {
        return openingBank;
    }

    public void setOpeningBank(String openingBank) {
        this.openingBank = openingBank;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
}
