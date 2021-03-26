<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="sidebar">
	<div class="site-width">
		<!-- START: Menu-->
		<ul id="side-menu" class="sidebar-menu">
			<li class="dropdown">
			<a href="#" style="text-align: center; font-size: 15px;">계약 </a></li>
		</ul>
	</div>
	<div class="site-width">
		<ul class="list-unstyled inbox-nav  mb-0 mt-2 notes-menu" id="myTab1"
			role="tablist">
				<li class="nav-item">
				<a class="nav-link active" href="${cp }/contract/contractList?p_state=04&user_id=${S_USER.user_id}"> 
					<i class="icon-list">계약 중인 목록
					</i>
				</a>
				</li>
				<li class="nav-item">
					<a class="nav-link" href="${cp }/contract/contractList?p_state=06&p_state2=07&p_state3=09&user_id=${S_USER.user_id}">
					<input type="hidden" name="type" value="f">
					<i class="icon-star">완료된 계약 목록</i>
				</a>
				</li>
				<li class="nav-item">
				<a class="nav-link" href="#">
					<i class="icon-star">일정 등록</i>
				</a>
			</li>
		</ul>
	</div>
</div>
<script>
//문서 로딩이 완료되고 나서 실행되는 영역
$(function(){
	$(".todo-content").on("click", function(){
		 var pcode = $(this).data("p_code");
		$(".p_code").val(pcode);
		console.log(pcode);
		$("#frm").submit();
	});
});
</script>


<style>
.nav-link:hover {
	color: var(- -primarycolor);
}

.todo-item:hover {
	background: var(- -sidebarbordercolor);
}
</style>

<!-- START: Main Content-->
<main>
	<div class="container-fluid site-width">
		<!-- START: Breadcrumbs-->
		<!-- END: Breadcrumbs-->

		<!-- START: Card Data-->
		<div class="row">
			<div class="col-11 mt-3">
				<div class="card">
					<div class="col-12" style="padding-top: 0px;">
						<div class="card-header border-bottom p-2 d-flex">
							<div class="input-group row col-6" style="height: 36px;">
<%-- 								<form class="input-group row" action="${cp }/board/searchView" method="post"> --%>
<%-- 									<input type="hidden" id="b_no" name="b_no" value="${bno }" /> <input --%>
<!-- 										class="form-control" type="text" id="key" name="keyword" -->
<!-- 										placeholder="검색어를 입력하세요." value=""> <span -->
<!-- 										class="input-group-append"> -->
<!-- 										<button class="btn btn-primary" type="submit" id="searchBtn" -->
<!-- 											data-card-widget="search"> -->
<!-- 											<i class="fa fa-fw fa-search"></i> -->
<!-- 										</button> -->
<!-- 									</span> -->
<!-- 								</form> -->
<%-- 									<a href="${cp }/project/insertContract" class="bg-primary py-2 px-2 rounded ml-auto text-white" data-target="#newtodo"> --%>
<!--                                     <i class="icon-plus align-middle text-white"></i> <span>계약 일정 등록</span> -->
<!--                                 </a> -->
							</div>
						</div>

						<div class="card-body p-0">
							<ul class="todo-list" style="margin-bottom: 0px;">
								<c:forEach items="${projectList }" var="project">
									<%--                                       	<c:if test="${project.p_state eq '04' }"> --%>
									<form id="frm" action="${cp }/contract/selectContractUser">
									<input type="hidden" class="user_id" name="user_id" value="${S_USER.user_id}" />
									<input type="hidden" class="p_code" name="p_code" value="" />
									<input type="hidden" id="p_state" name="p_state" value="${param.p_state }" />
									<input type="hidden" id="state" name="state1" value="${state1 }" />
									<input type="hidden" id="state" name="state2" value="${state2 }" />
									<input type="hidden" id="state" name="state3" value="${state3 }" />
									<input type="hidden" name="type" value="${type }">
									</form>
									<li class="todo-item">
										<div class="todo-content" data-p_code="${project.p_code }">
											<h3>프로젝트 : ${project.p_title }</h3>
											<p class="text-muted mb-0 font-weight-bold todo-date">프로젝트
												기간 : ${project.period }일</p>
											<p class="small-content text-muted mb-0">지원자</p>
										</div>
										<div class="ml-auto">
											<a class="dropdown-item edit-todo" href="${cp }/contract/selectContractUser?user_id=${S_USER.user_id}&p_code=${project.p_code }&p_state=${param.p_state}&state1=${state1}&state2=${state2}&state3=${state3}&type=${type}"><i class="icon-exclamation font-15 mr-2"></i></a>
										</div>
										<div>
											<a href="#" data-toggle="dropdown" aria-haspopup="true"
												aria-expanded="false">
												<i class="icon-options-vertical font-15"></i></a>
											<div class="dropdown-menu p-0 m-0 dropdown-menu-right">
												<a class="dropdown-item edit-todo" href="${cp }/contract/selectContractUser?user_id=${S_USER.user_id}&p_code=${project.p_code }&p_state=${param.p_state}&state1=${state1}&state2=${state2}&state3=${state3}&type=${type}">상세보기</a>
												 <a class="dropdown-item delete" href="#">삭제</a>
											</div>
										</div></li>
									<%--                                         </c:if> --%>
								</c:forEach>
							</ul>
							<div class="col-sm-12 col-md-12"
								style="padding-top: 15px; border-top: 1px solid rgba(72, 94, 144, 0.2);">
								<div class="dataTables_paginate paging_simple_numbers"
									id="dataTable_paginate"
									style="float: right; margin-right: 43%;">
									<ul class="pagination justify-content-center m-0">
										<c:set var="cnt" value="${pagination}" />
										<li class="page-item">
										<a class="page-link"
											href="${cp }/project/contractList?user_id=${S_USER.user_id }&p_state=${param.p_state}&page=1&pageSize=${pageVo.pageSize}"><i
												class="fas fa-angle-double-left"></i></a></li>
										<li class="page-item"><a class="page-link"
											href="${cp }/project/contractList?user_id=${S_USER.user_id }&p_state=${param.p_state}&page=
										<c:choose>
										<c:when test="${pageVo.getPage() > 0 }">${pageVo.page}</c:when>
										</c:choose>&pageSize=${pageVo.pageSize}"><i
												class="fas fa-angle-left"></i></a></li>
										<c:forEach begin="1" end="${pagination}" var="i">
											<c:choose>
												<c:when test="${pageVo.page == i}">
													<li class="page-item active"><a class="page-link"
														href="${cp}/project/contractList?user_id=${S_USER.user_id }&p_state=${param.p_state}&page=${i}&pageSize=${pageVo.pageSize}">${i}</a></li>
												</c:when>
												<c:otherwise>
													<li class="page-item"><a class="page-link"
														href="${cp}/project/contractList?user_id=${S_USER.user_id }&p_state=${param.p_state}&page=${i}&pageSize=${pageVo.pageSize}">${i}</a></li>
												</c:otherwise>
											</c:choose>
										</c:forEach>
										<li class="page-item">
										<a class="page-link" href="${cp}/project/contractList?user_id=${S_USER.user_id }&p_state=${param.p_state}&page=
										<c:choose>
										<c:when test="${pageVo.getPage() + 1 > pagination }">${pagination }</c:when>
										<c:otherwise>${pageVo.getPage() + 1}</c:otherwise>
										</c:choose>&pageSize=${pageVo.pageSize }">
										<i class="fas fa-angle-right"></i></a></li>
										<li class="page-item">
										<a class="page-link" href="${cp}/project/contractList?user_id=${S_USER.user_id }&page=${pagination}&pageSize=${pageVo.pageSize}&p_state=${param.p_state}"><i
												class="fas fa-angle-double-right"></i></a></li>
									</ul>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- END: Card DATA-->
	</div>
</main>
<!-- END: Content-->
