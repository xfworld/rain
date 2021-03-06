create DATABASE IF NOT EXISTS overseapay CHARSET  utf8;

USE overseapay;
CREATE TABLE IF NOT EXISTS `o_pay_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `applicationCode` int(4) NOT NULL,
  `channelId` varchar(10) NOT NULL COMMENT '支付渠道id',
  `amount` int(4)  DEFAULT NULL COMMENT '支付金额，当为卡类型时传0',
  `currencyCode` VARCHAR(3)  NULL  COMMENT '币种，卡类型时0',
  `version` VARCHAR(5)  DEFAULT 'v1.0' COMMENT '版本有变',
  `referenceId` varchar(100) DEFAULT NULL  COMMENT '商户生成的唯一订单id',
  `paymentId` varchar(50) DEFAULT NULL COMMENT '第三方返回的支付流水号',
  `paymentUrl` varchar(255) DEFAULT NULL COMMENT '第三方返回的支付流水号',
  `paymentStatusCode` varchar(30) DEFAULT NULL COMMENT '返回状态',
  `returnUrl` varchar(255) DEFAULT NULL,
  `customerId` int(4) DEFAULT NULL,
  `type` int(4) DEFAULT 0 COMMENT '支付类型，0 mol',
  `createtime` varchar(100) DEFAULT NULL,
  `paymentStatusDate` varchar(100) DEFAULT NULL COMMENT '返回的完成时间',
  `ip` varchar(100) DEFAULT NULL,
  `status` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_order` (`referenceId`)
) ENGINE=INNODB  DEFAULT CHARSET=utf8 COMMENT='订单表';


CREATE TABLE IF NOT EXISTS `o_pay_dic` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `appid` int(4) NOT NULL,
  `ip` varchar(100) DEFAULT NULL,
  `callbackurl` varchar(100) DEFAULT NULL,
  `createtime` varchar(100) DEFAULT NULL,
  `privatekey` varchar(50) NOT NULL,
  `status` int(11) DEFAULT '0',
  `notes` varchar(255) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `userid` int(4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_app` (`appid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='支付字典表';