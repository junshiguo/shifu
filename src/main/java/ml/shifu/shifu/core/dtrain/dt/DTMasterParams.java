/*
 * Copyright [2013-2015] PayPal Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ml.shifu.shifu.core.dtrain.dt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ml.shifu.guagua.io.HaltBytable;

/**
 * TODO
 * 
 * @author Zhang David (pengzhang@paypal.com)
 */
public class DTMasterParams extends HaltBytable {

    /**
     * All trees
     */
    private List<TreeNode> trees;

    /**
     * nodeIndexInGroup => (treeId, Node); nodeIndexInGroup is starting from 0 in each iteration.
     */
    private Map<Integer, TreeNode> todoNodes;

    private long count;

    private double squareError;

    public DTMasterParams() {
    }

    public DTMasterParams(long count, double squareError) {
        super();
        this.count = count;
        this.setSquareError(squareError);
    }

    public DTMasterParams(List<TreeNode> trees, Map<Integer, TreeNode> todoNodes) {
        this.trees = trees;
        this.todoNodes = todoNodes;
    }

    /**
     * @return the trees
     */
    public List<TreeNode> getTrees() {
        return trees;
    }

    /**
     * @return the todoNodes
     */
    public Map<Integer, TreeNode> getTodoNodes() {
        return todoNodes;
    }

    /**
     * @param trees
     *            the trees to set
     */
    public void setTrees(List<TreeNode> trees) {
        this.trees = trees;
    }

    /**
     * @param todoNodes
     *            the todoNodes to set
     */
    public void setTodoNodes(Map<Integer, TreeNode> todoNodes) {
        this.todoNodes = todoNodes;
    }

    @Override
    public void doWrite(DataOutput out) throws IOException {
        out.writeLong(count);
        out.writeDouble(getSquareError());

        assert trees != null;
        out.writeInt(trees.size());
        for(TreeNode node: trees) {
            node.write(out);
        }

        if(todoNodes == null) {
            out.writeInt(0);
        } else {
            out.writeInt(todoNodes.size());
            for(Map.Entry<Integer, TreeNode> node: todoNodes.entrySet()) {
                out.writeInt(node.getKey());
                node.getValue().write(out);
            }
        }
    }

    @Override
    public void doReadFields(DataInput in) throws IOException {
        this.count = in.readLong();
        this.setSquareError(in.readDouble());

        int treeNum = in.readInt();
        this.trees = new ArrayList<TreeNode>(treeNum);
        for(int i = 0; i < treeNum; i++) {
            TreeNode treeNode = new TreeNode();
            treeNode.readFields(in);
            this.trees.add(treeNode);
        }

        int todoNodesSize = in.readInt();
        if(todoNodesSize > 0) {
            todoNodes = new HashMap<Integer, TreeNode>(todoNodesSize, 1f);
            for(int i = 0; i < todoNodesSize; i++) {
                int key = in.readInt();
                TreeNode treeNode = new TreeNode();
                treeNode.readFields(in);
                todoNodes.put(key, treeNode);
            }
        }
    }

    /**
     * @return the count
     */
    public long getCount() {
        return count;
    }

    /**
     * @param count
     *            the count to set
     */
    public void setCount(long count) {
        this.count = count;
    }

    /**
     * @return the squareError
     */
    public double getSquareError() {
        return squareError;
    }

    /**
     * @param squareError the squareError to set
     */
    public void setSquareError(double squareError) {
        this.squareError = squareError;
    }
}