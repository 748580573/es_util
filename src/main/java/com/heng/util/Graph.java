package com.heng.util;

import com.heng.exception.CycleReferenceException;

import java.util.*;

/**
 * 图，
 * @param T 数据类型
 */
public class Graph<T> {

    private Map<T,Node> nodeMap = new HashMap<>();                   //点集

    private Set<Side> sideSet = new HashSet<>();                     //边集
//    private List<Side> sideSet = new ArrayList<>();                  //边集

    /**
     * 添加节点
     * @param data
     */
    public void addNode(T data){
        if (nodeMap.get(data) == null){
            Node node = new Node(data);
            nodeMap.put(data,node );
        }
    }

    /**
     * 获取节点
     * @param data
     * @return
     */
    public Node getNode(T data){
        return nodeMap.get(data);
    }

    /**
     * 添加边，边的方向为 preData -----> currentData
     * @param preData
     * @param currentData
     */
    public void addSide(T preData,T currentData) throws CycleReferenceException {
        Side side = new Side(preData,currentData );
        addNode(preData);
        addNode(currentData);

        Node preNode = getNode(preData);
        Node currentNode = getNode(currentData);

        if(!preNode.rearNodes.contains(currentNode)){
            preNode.rearNodes.add(currentNode);
        }

        if (!currentNode.preNodes.contains(preNode)){
            currentNode.preNodes.add(preNode);
        }
        if (!sideSet.contains(side)){
            sideSet.add(side);
        }

        if (checkExistCycle(preNode)){
            throw new CycleReferenceException("循环引用异常," + preData + "----->" + currentData + "循环异常");
        }
    }

    public boolean checkExistCycle(Node node){
        clear();

        Stack<Node> stack = new Stack<>();
        node.status = 1;
        stack.add(node);
        while (!stack.isEmpty()){
            Node tempNode = stack.pop();
            if (!tempNode.rearNodes.isEmpty()){
                for (Node n : tempNode.rearNodes){
                    if (tempNode.status == 1 && n.status == 1){
                        return true;
                    }
                    n.status = 1;
                    stack.add(n);
                }
            }
        }
        return false;
    }

    /**
     * 清除所有节点的状态
     */
    private void clear(){
        for (Map.Entry<T,Node> entry : nodeMap.entrySet()){
            entry.getValue().status = 0;
        }
    }


    /**
     * 点
     */
    private class Node{

        private int status = 0;                                    //遍历图的时候用到，1表示起始节点，0表示未被遍历的节点，-1表示被遍历过的节点

        private T data;                                            //data

        private int outDegree = 0;                                //出度

        private int intoDegree = 0;                               //入度

        private Set<Node> preNodes = new HashSet<>();             //该点的前一个点的集合

        private Set<Node> rearNodes = new HashSet<>();            //该点的后一个点的集合


        public Node(T clazz){
            this.data = clazz;
        }

        @Override
        public int hashCode() {
            return this.data.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            Node anotherNode = (Node)obj;
            return anotherNode.data == this.data;
        }
    }

    /**
     * 边（两点形成一边）
     */
    private class Side{

        private Node currentNode;                                //当前点

        private Node anotherNode;                                //另外一个点

        public Side(Node anotherNode,Node currentNode){
            this.currentNode = currentNode;
            this.anotherNode = anotherNode;
        }

        public Side(T currentData,T anotherData){
            this.currentNode = new Node(currentData);
            this.anotherNode = new Node(anotherData);
        }

        @Override
        public boolean equals(Object obj) {
            Side anotherSide = (Side)obj;
            if (anotherSide.anotherNode.data != anotherNode.data){
                return false;
            }else if (anotherSide.currentNode.data != currentNode.data){
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return  Objects.hash(currentNode.data, anotherNode.data);
        }
    }
}
