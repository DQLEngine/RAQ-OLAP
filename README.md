# 润乾OLAP
常规OLAP需要根据业务分析需求预先把相关表关联起来，制作成用于OLAP需要的CUBE，而润乾OLAP是一种不用频繁制作CUBE的敏捷OLAP，仅需要对整个数据库关联的表做一次元数据预定义。就能针对整库的所有查询分析自动关联，比如下面的分析，经理姓名来自【员工表】的【部门】的【部门经理】的【姓名】，不会感觉到表关联，但实际获得的数据，是通过对员工表、部门表进行了互相关联查得：<br>
<img src="http://img.raqsoft.com.cn/docx/1614069868548100.png"/><br>

这种自动的关联能力来自一种新的查询语言DQL，并非直接使用数据库直接支持的SQL，看上面这个分析的DQL语句：<br><br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SELECT <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;姓名 AS 员工姓名<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;,部门.部门经理.姓名 AS 经理姓名<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;FROM 员工表<br>
<br>
以及相对应的SQL语句：<br><br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SELECT<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;T1.姓名 AS 员工姓名<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;,T3.姓名 AS 经理姓名<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;FROM<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;员工表 T1<br> 
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;LEFT JOIN 部门表 T2 ON T1.部门=T2.部门ID<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;LEFT JOIN 员工表 T3 ON T2.部门经理=T3.员工ID<br>
<br>	
润乾OLAP按照分析界面操作，容易拼出来相应的DQL语句，但DQL语句无法直接去数据库查获数据。这时需要一个独立的DQL引擎服务器，提供查询服务，它负责DQL转换SQL，并从封装起来的底层数据库查得数据。在整个系统的架构图中，DQL服务器就等价于常规的数据库服务器，只是它的查询语言是换成了能做自动关联的DQL。<br>
<br>
目前市面上已知的OLAP、BI相关产品，无论设计成何种形态，都没有跳脱针对具体业务预建模(CUBE、宽表)的模式，根本原因就在于无法解决表自动关联的问题。DQL服务器补足了这方面能力，基于它容易开发出真正敏捷的OLAP系统来。润乾OLAP就是这样的一个WEB系统，并且开源源代码，给扩展外围功能提供方便；也可以只是借鉴，重新设计实现一套适用于自己行业的敏捷OLAP。<br>
<br>
# 如何使用
润乾OLAP、DQL服务器、Demo数据库等功能模块整体打包进了同一个安装包，可以从乾学院下载：<br>
<a href="http://c.raqsoft.com.cn/tag/Download">下载润乾报表</a><br>
<br>
润乾OLAP的WEB根路径是{润乾报表安装目录}\report\web\webapps\demo\；<br>
该项目的开源的Java源码依赖WEB-INF\lib\*.jar；<br>
源码被编译后存入了WEB-INF\lib\guide.jar，修改重新编译后，可以更新到这个jar里；<br>
jsp、js等WEB资源在raqsoft\guide\jsp、raqsoft\guide\js目录下，可根据需求直接修改。<br>
<br>
该项目有一系列的实践文章，方便了解学习<br>
<a href="http://c.raqsoft.com.cn/article/1600818947479">润乾报表开源 BI 组件学习</a><br>
