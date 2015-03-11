package org.david.rain.pay.client.service;

import org.apache.commons.lang3.StringUtils;
import org.david.rain.pay.client.dao.Idao;
import org.david.rain.pay.client.dao.entity.OpayOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * User: david
 * Date: 14-7-4
 * Time: 下午5:22
 */

@Service
public class PayService {

public static final String PAY_TABLE = "o_pay_order";
    @Autowired
    private Idao wdao;

    public static final Logger LOG = LoggerFactory.getLogger(PayService.class);



    public int saveOrder(OpayOrder opayOrder) {

        try {
            return wdao.insert(opayOrder);
        } catch (SQLException e) {
            LOG.error("insert order[ opayOrder:{}]  failed :{}",opayOrder,e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public boolean ifFirstOrder(String orderid) {
        try {
            long  re = wdao.queryScalar("select count(1) from " + PAY_TABLE + " where referenceId = ? ",orderid);
            return re == 0   ;
        } catch (SQLException e) {
            LOG.error("queryOrderNum [orderid:{}]failed :{}",orderid,e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int updateOrder(OpayOrder opayOrder,String type) {
        try {
        if(StringUtils.equals(type,"callback")){
                return wdao.update("update " + PAY_TABLE + " set status = ? , paymentStatusCode = ?, paymentStatusDate = ? where referenceId = ? ",opayOrder.getStatus(),opayOrder.getPaymentStatusCode(),opayOrder.getPaymentStatusDate(),opayOrder.getReferenceId());
            }else{
                return wdao.update("update " + PAY_TABLE + " set status = ? , paymentId = ? where referenceId = ? ",opayOrder.getStatus(),opayOrder.getPaymentId(),opayOrder.getReferenceId());
        }
        } catch (SQLException e) {
            LOG.error("updateOrder[ opayOrder:{}] , type[] === failed :{}",opayOrder,type,e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public OpayOrder getOpayOrderByOrderId(String referenceId) {
        try {
            return  wdao.queryObject(OpayOrder.class,"select * from " + PAY_TABLE + " where referenceId = ? ",referenceId);
        } catch (SQLException e) {
            LOG.error("getOpayOrderByOrderId[ referenceId:{}] === failed :{}", referenceId,e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public  String getApplicationCodeByReferenceId(String referenceId){
        String query = "select  b.callbackUrl from "+PAY_TABLE+" a ,o_pay_dic b where a.applicationCode = b.appid and a.referenceId = ? ";
        try {
            return wdao.queryScalar(query,referenceId);
        } catch (SQLException e) {
            LOG.error("getApplicationCodeByReferenceId[ referenceId:{}] === failed :{}", referenceId,e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
