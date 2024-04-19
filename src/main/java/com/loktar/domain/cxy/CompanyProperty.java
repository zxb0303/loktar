package com.loktar.domain.cxy;

import java.io.Serializable;

public class CompanyProperty implements Serializable {
    private Integer id;

    private String zhuti;

    private String zichanbianhao;

    private String shebeimingcheng;

    private String pinpai;

    private String xinghao;

    private String shuliang;

    private String danjia;

    private String jine;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getZhuti() {
        return zhuti;
    }

    public void setZhuti(String zhuti) {
        this.zhuti = zhuti == null ? null : zhuti.trim();
    }

    public String getZichanbianhao() {
        return zichanbianhao;
    }

    public void setZichanbianhao(String zichanbianhao) {
        this.zichanbianhao = zichanbianhao == null ? null : zichanbianhao.trim();
    }

    public String getShebeimingcheng() {
        return shebeimingcheng;
    }

    public void setShebeimingcheng(String shebeimingcheng) {
        this.shebeimingcheng = shebeimingcheng == null ? null : shebeimingcheng.trim();
    }

    public String getPinpai() {
        return pinpai;
    }

    public void setPinpai(String pinpai) {
        this.pinpai = pinpai == null ? null : pinpai.trim();
    }

    public String getXinghao() {
        return xinghao;
    }

    public void setXinghao(String xinghao) {
        this.xinghao = xinghao == null ? null : xinghao.trim();
    }

    public String getShuliang() {
        return shuliang;
    }

    public void setShuliang(String shuliang) {
        this.shuliang = shuliang == null ? null : shuliang.trim();
    }

    public String getDanjia() {
        return danjia;
    }

    public void setDanjia(String danjia) {
        this.danjia = danjia == null ? null : danjia.trim();
    }

    public String getJine() {
        return jine;
    }

    public void setJine(String jine) {
        this.jine = jine == null ? null : jine.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", zhuti=").append(zhuti);
        sb.append(", zichanbianhao=").append(zichanbianhao);
        sb.append(", shebeimingcheng=").append(shebeimingcheng);
        sb.append(", pinpai=").append(pinpai);
        sb.append(", xinghao=").append(xinghao);
        sb.append(", shuliang=").append(shuliang);
        sb.append(", danjia=").append(danjia);
        sb.append(", jine=").append(jine);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}