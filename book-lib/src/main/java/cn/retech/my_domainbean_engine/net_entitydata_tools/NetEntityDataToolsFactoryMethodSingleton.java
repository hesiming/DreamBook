package cn.retech.my_domainbean_engine.net_entitydata_tools;

import cn.retech.my_domainbean_engine.net_entitydata_tools.dream_book.NetRequestEntityDataPackageForDreamBook;
import cn.retech.my_domainbean_engine.net_entitydata_tools.dream_book.NetRespondEntityDataUnpackDreamBook;
import cn.retech.my_domainbean_engine.net_entitydata_tools.dream_book.ServerRespondDataTestDreamBook;
import cn.retech.my_domainbean_engine.net_entitydata_tools.interfaces.INetRequestEntityDataPackage;
import cn.retech.my_domainbean_engine.net_entitydata_tools.interfaces.INetRespondRawEntityDataUnpack;
import cn.retech.my_domainbean_engine.net_entitydata_tools.interfaces.IServerRespondDataTest;

/**
 * 这里使用了工厂方法设计模式
 * 
 * @author skyduck
 */
public enum NetEntityDataToolsFactoryMethodSingleton implements INetEntityDataTools {
	getInstance;

	@Override
	public INetRequestEntityDataPackage getNetRequestEntityDataPackage() {
		return new NetRequestEntityDataPackageForDreamBook();
	}

	@Override
	public INetRespondRawEntityDataUnpack getNetRespondEntityDataUnpack() {
		return new NetRespondEntityDataUnpackDreamBook();
	}

	@Override
	public IServerRespondDataTest getServerRespondDataTest() {
		return new ServerRespondDataTestDreamBook();
	}

}
