<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style type="text/css"> 
body,table{ 
font-size:12px; 
} 
table{ 
table-layout:fixed; 
empty-cells:show; 
border-collapse: collapse; 
margin:0 auto; 
} 
td{ 
height:30px; 
} 
h1,h2,h3{ 
font-size:12px; 
margin:0; 
padding:0; 
} 
.table{ 
border:1px solid #cad9ea; 
color:#666; 
} 
.table th { 
background-repeat:repeat-x; 
height:30px; 
} 
.table td,.table th{ 
border:1px solid #cad9ea; 
padding:0 1em 0; 
} 
.table tr.alter{ 
background-color:#f5fafe; 
} 
</style> 
<div align="center" style="text-align: center;width:100%;height:100%;background:url('image/common/home-bg.jpg');">
	
	<table>
		<thead>
			<tr>
				<th>最新通知信息！</th>
			</tr>
		</thead>
	</table>
	<table width="90%" class="table"> 
		<tr> 
			<th>通知标题</th> 
			<th>通知内容</th> 
			<th>发布时间</th> 
		</tr> 
		<c:forEach items="${noticeList}" var="notice" varStatus="id">
		<tr class="alter"> 
			<td>${notice.title }</td> 
			<td>${notice.content }</td> 
			<td>${notice.createTime }</td> 
		</tr> 
		</c:forEach>
</table> 
</div>
