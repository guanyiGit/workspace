# -*- coding=utf-8 -*-
# python中使用redis前需要安装redis模块  pip install redis
import redis

# 将特征值模型文件数据存入redis数据库，将用户历史下载数据存入redis,将app基本描述商品词表存入redis数据库
pool = redis.ConnectionPool(host='node4', port='6379', db=2)
r = redis.Redis(connection_pool=pool)

f = open('./ModelFile.txt', "rb")
while True:
    lines = f.readlines(100)
    if not lines:
        break
    for line in lines:
        kv = line.decode("utf-8").split('\t')
        r.hset("rcmd_features_score", kv[0], kv[1])

f = open('./UserItemsHistory.txt', "rb")
while True:
    lines = f.readlines(100)
    if not lines:
        break
    for line in lines:
        kv = line.decode("utf-8").split('\t')
        r.hset('rcmd_user_history', kv[0], kv[1])

f = open('./ItemList.txt', "rb")
while True:
    lines = f.readlines(100)
    if not lines:
        break
    for line in lines:
        kv = line.decode("utf-8").split('\t')
        # line[:-2] 取line 字符串的开头到倒数第二个的位置 数据，含头不含尾，也就是-2 就是将s 字符串中倒数后两个字符删除，常用在从文本读入数据的时候消除换行符的影响
        r.hset('rcmd_item_list', kv[0], line[:-2])
print('all finished...')
f.close()
