package com.loktar.domain.second;

import java.io.Serializable;
import java.util.Date;

public class SecondHandHouse implements Serializable {
    private Integer id;

    private String fwtybh;

    private String xzqhname;

    private String cqmc;

    private String xqmc;

    private Float jzmj;

    private String wtcsjg;

    private String mdmc;

    private String gplxrxm;

    private String scgpshsj;

    private String status;

    private Date statusTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFwtybh() {
        return fwtybh;
    }

    public void setFwtybh(String fwtybh) {
        this.fwtybh = fwtybh == null ? null : fwtybh.trim();
    }

    public String getXzqhname() {
        return xzqhname;
    }

    public void setXzqhname(String xzqhname) {
        this.xzqhname = xzqhname == null ? null : xzqhname.trim();
    }

    public String getCqmc() {
        return cqmc;
    }

    public void setCqmc(String cqmc) {
        this.cqmc = cqmc == null ? null : cqmc.trim();
    }

    public String getXqmc() {
        return xqmc;
    }

    public void setXqmc(String xqmc) {
        this.xqmc = xqmc == null ? null : xqmc.trim();
    }

    public Float getJzmj() {
        return jzmj;
    }

    public void setJzmj(Float jzmj) {
        this.jzmj = jzmj;
    }

    public String getWtcsjg() {
        return wtcsjg;
    }

    public void setWtcsjg(String wtcsjg) {
        this.wtcsjg = wtcsjg == null ? null : wtcsjg.trim();
    }

    public String getMdmc() {
        return mdmc;
    }

    public void setMdmc(String mdmc) {
        this.mdmc = mdmc == null ? null : mdmc.trim();
    }

    public String getGplxrxm() {
        return gplxrxm;
    }

    public void setGplxrxm(String gplxrxm) {
        this.gplxrxm = gplxrxm == null ? null : gplxrxm.trim();
    }

    public String getScgpshsj() {
        return scgpshsj;
    }

    public void setScgpshsj(String scgpshsj) {
        this.scgpshsj = scgpshsj == null ? null : scgpshsj.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(Date statusTime) {
        this.statusTime = statusTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", fwtybh=").append(fwtybh);
        sb.append(", xzqhname=").append(xzqhname);
        sb.append(", cqmc=").append(cqmc);
        sb.append(", xqmc=").append(xqmc);
        sb.append(", jzmj=").append(jzmj);
        sb.append(", wtcsjg=").append(wtcsjg);
        sb.append(", mdmc=").append(mdmc);
        sb.append(", gplxrxm=").append(gplxrxm);
        sb.append(", scgpshsj=").append(scgpshsj);
        sb.append(", status=").append(status);
        sb.append(", statusTime=").append(statusTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}