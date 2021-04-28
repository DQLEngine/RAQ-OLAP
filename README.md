# RAQ-OLAP
润乾OLAP是一种不用频繁制作CUBE的敏捷OLAP，仅需要对整个数据库关联的表做一次元数据预定义。<br>
它的敏捷能力来自一种新的查询语言DQL，并非直接使用数据库直接支持的SQL，DQL引擎是一个单独的Server，可以理解它等价于常规数据库Server，只是多了敏捷OLAP能力。<br>
WEB系统中，借助DQL Server这种新数据源，就能比较容易的实现敏捷OLAP。<br>
<br>
润乾OLAP的WEB系统是一个开源项目，简单的演示了如果借助DQL引擎实现敏捷OLAP，改造或借鉴该开源代码，可以开发出适用于自己行业的敏捷OLAP系统。<br>
# 如何使用
润乾OLAP、DQL Server、Demo数据库等功能模块整体打包进了同一个安装包，可以从乾学院下载：<br>
<a href="http://c.raqsoft.com.cn/tag/Download">下载润乾报表</a><br>
<br>
润乾OLAP的WEB系统根路径是{润乾报表安装目录}\report\web\webapps\demo\；<br>
该项目的开源的Java源码依赖WEB-INF\lib\*.jar；<br>
源码被编译后存入了WEB-INF\lib\guide.jar，修改重新编译后，可以更新到这个jar里；<br>
jsp、js等WEB资源在raqsoft\guide\jsp、raqsoft\guide\js目录下，可根据需求直接修改。<br>
<br>
该项目有一系列的实践文章，方便了解学习<br>
<a href="http://c.raqsoft.com.cn/article/1600818947479">润乾报表开源 BI 组件学习</a><br>
