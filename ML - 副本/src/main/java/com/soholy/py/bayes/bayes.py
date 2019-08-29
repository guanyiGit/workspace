# coding:utf-8

import os
import sys
# codecs 编码转换模块
import codecs

# 讲训练样本中的中文文章分词并存入文本文件中

# if __name__ == '__main__':
#     corpus = []
#     f = codecs.open("D:/workspaceR/news_spam.csv", "r", "utf-8")  
#     f1 = codecs.open("D:/workspaceR/news_spam_jieba.csv", "w", "utf-8")
#     count = 0
#     while True:  
#         line = f.readline()  
#         if line:  
#             count = count + 1
#             line = line.split(",")
#             s = line[1]
#             words=pseg.cut(s)
#             temp = []
#             for key in words:
#                 temp.append(key.word)
#             sentence = " ".join(temp)
#             print line[0],',',sentence
#             corpus.append(sentence)
#             f1.write(line[0])
#             f1.write(',')
#             f1.write(sentence)
#             f1.write('\n')
#         else:  
#             break
#     f.close()
#     f1.close()


# #####################################################
# Multinomial Naive Bayes Classifier
from sklearn.naive_bayes import MultinomialNB
from sklearn.feature_extraction.text import CountVectorizer

print('*************************\nNaive Bayes\n*************************')

if __name__ == '__main__':
    # 读取文本构建语料库
    corpus = []
    labels = []
    corpus_test = []
    labels_test = []
    f = codecs.open("./sms_spam.txt", "rb")
    count = 0
    while True:
        # readline() 方法用于从文件读取整行，包括 "\n" 字符。
        line = f.readline().decode("utf-8")
        # 读取第一行，第一行数据是列头，不统计
        if count == 0:
            count = count + 1
            continue
        if line:
            count = count + 1
            line = line.split(",")
            label = line[0]
            sentence = line[1]
            corpus.append(sentence)
            if "ham" == label:
                labels.append(0)
            elif "spam" == label:
                labels.append(1)
            if count > 5550:
                corpus_test.append(sentence)
                if "ham" == label:
                    labels_test.append(0)
                elif "spam" == label:
                    labels_test.append(1)
        else:
            break
    # 文本特征提取：
    #     将文本数据转化成特征向量的过程
    #     比较常用的文本特征表示法为词袋法
    #
    # 词袋法：
    #     不考虑词语出现的顺序，每个出现过的词汇单独作为一列特征
    #     这些不重复的特征词汇集合为词表
    #     每一个文本都可以在很长的词表上统计出一个很多列的特征向量
    # CountVectorizer是将文本向量转换成稀疏表示数值向量（字符频率向量）  vectorizer 将文档词块化,只考虑词汇在文本中出现的频率
    # 词袋
    vectorizer = CountVectorizer()
    # 每行的词向量，fea_train是一个矩阵
    fea_train = vectorizer.fit_transform(corpus)

    print("vectorizer.get_feature_names is ", vectorizer.get_feature_names())
    print("fea_train is ", fea_train.toarray())

    # vocabulary=vectorizer.vocabulary_ 只计算上面vectorizer中单词的tf(term frequency 词频)
    vectorizer2 = CountVectorizer(vocabulary=vectorizer.vocabulary_)
    fea_test = vectorizer2.fit_transform(corpus_test)
    #     print vectorizer2.get_feature_names()
    #     print fea_test.toarray()

    # create the Multinomial Naive Bayesian Classifier
    # alpha = 1 拉普拉斯估计给每个单词个数加1
    clf = MultinomialNB(alpha=1)
    clf.fit(fea_train, labels)

    pred = clf.predict(fea_test);
    for p in pred:
        if p == 0:
            print("正常邮件")
        else:
            print("垃圾邮件")
