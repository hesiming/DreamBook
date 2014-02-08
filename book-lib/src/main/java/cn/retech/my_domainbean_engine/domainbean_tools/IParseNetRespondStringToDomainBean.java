package cn.retech.my_domainbean_engine.domainbean_tools;

/**
 * 将目标网络业务接口返回的响应字符串, 解析该业务接口对应的 "网络响应业务Bean"
 * @author skyduck
 * 
 */
public interface IParseNetRespondStringToDomainBean {
	public Object parseNetRespondStringToDomainBean(final String netRespondString) throws Exception;
}
