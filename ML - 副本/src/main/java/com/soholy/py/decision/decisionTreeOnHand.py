#coding:utf-8  


import numpy as np
import math as mt
from collections import defaultdict

class DecisionTree(object):
    def __init__(self):
        pass

#   计算概率
    def _getDistribution(self,dataArray):
        distribution = defaultdict(float)
        m, n = np.shape(dataArray)
        for line in dataArray:
            print(line[-1])
            distribution[line[-1]] += 1.0/m
        return distribution

#   计算信息熵
    def _entropy(self, dataArray):
        ent = 0.0
        distribution=self._getDistribution(dataArray)
        for key, prob in distribution.items():
            ent -= prob * mt.log(prob, 2)
        return ent

    def _conditionEntropy(self, dataArray, colIdx):
        valueCnt = defaultdict(int)
        m, n = np.shape(dataArray)
        condEnt = 0.0
        uniqueValues = np.unique(dataArray[:, colIdx])
        for oneValue in uniqueValues:
            oneData = dataArray[dataArray[:, colIdx] == oneValue]
            oneEnt = self._entropy(oneData)
            prob = float(np.shape(oneData)[0]) / m
            condEnt += prob * oneEnt
        return condEnt

    def _infoGain(self,dataArray,colIdx,baseEnt):
        condEnt=self._conditionEntropy(dataArray,colIdx)
        return baseEnt-condEnt

    def _chooseBestProp(self,dataArray):
        m,n = np.shape(dataArray)
        bestProp = -1
        bestInfoGain=0
        baseEnt = self._entropy(dataArray)
        for i in range(n-1):
            infoGain=self._infoGain(dataArray,i,baseEnt)
            if infoGain > bestInfoGain:
                bestProp=i
                bestInfoGain=infoGain
        return bestProp

    def _splitData(self,dataArray,colIdx,splitValue):
        m,n=np.shape(dataArray)

        cols=np.array(range(n)) != colIdx
        rows=(dataArray[:,colIdx]==splitValue)

        # data=dataArray[rows,:][:,cols]
        #ix_  取rows中指定的行，取cols中指定的列   花式索引
        data=dataArray[np.ix_(rows,cols)]
        return data

    def createTree(self,dataArray):
#         colNames = ["age","income","student"]
        m,n=np.shape(dataArray)
        if len(np.unique(dataArray[:,-1])) == 1:
            return (dataArray[0,-1],1.0)
        if n==2:
            distribution=self._getDistribution(dataArray)
            sortProb=sorted(distribution.items(),key=lambda x:x[1],reverse=True)
            return sortProb
        rootNode = {}
        bestPropIdx=self._chooseBestProp(dataArray)
        rootNode[bestPropIdx] = {}
        uniqValues=np.unique(dataArray[:,bestPropIdx])
        for oneValue in uniqValues:
            splitDataArray=self._splitData(dataArray,bestPropIdx,oneValue)
            rootNode[bestPropIdx][oneValue]=self.createTree(splitDataArray)
        return rootNode
    
def loadData():
    dataMat = []                 
    fr = open("decisiontree.txt")
#     readlines他会一次性将decisiontree.txt文件全部加载到内存的列表中
    lines = fr.readlines()
    for line in lines:
        curLine = line.strip().split('\t')
        dataMat.append(curLine)
    return dataMat

if __name__ == '__main__':
    data = loadData()
    print(data)
    dataarray = np.array(data)
    dt=DecisionTree()
    tree=dt.createTree(dataarray)
    print(tree)
    import treePlotter as tp
    import matplotlib.pyplot as plt
 
    tp.createPlot(tree)
