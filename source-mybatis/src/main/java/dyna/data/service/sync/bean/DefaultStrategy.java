/*
 * Copyright (C) DCIS 版权所有
 * 功能描述: DefaultStrategy
 * WangLHB Jul 8, 2011
 */
package dyna.data.service.sync.bean;

import java.util.Map;

import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.strategy.Value;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.NodeMap;
import org.simpleframework.xml.stream.OutputNode;

/**
 * @author WangLHB
 * 
 */
public class DefaultStrategy implements Strategy
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simpleframework.xml.strategy.Strategy#read(org.simpleframework.xml.strategy.Type,
	 * org.simpleframework.xml.stream.NodeMap, java.util.Map)
	 */
	@Override
	public Value read(Type type, NodeMap<InputNode> node, @SuppressWarnings("rawtypes") Map map) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.simpleframework.xml.strategy.Strategy#write(org.simpleframework.xml.strategy.Type, java.lang.Object,
	 * org.simpleframework.xml.stream.NodeMap, java.util.Map)
	 */
	@Override
	public boolean write(Type type, Object value, NodeMap<OutputNode> node, @SuppressWarnings("rawtypes") Map map) throws Exception
	{
		return false;
	}

}