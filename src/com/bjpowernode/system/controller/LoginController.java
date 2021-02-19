package com.bjpowernode.system.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.bjpowernode.buss.entity.base.NoticeEntity;
import com.bjpowernode.buss.service.NoticeService;
import com.bjpowernode.common.util.AjaxJson;
import com.bjpowernode.common.util.ContextHolderUtils;
import com.bjpowernode.common.util.Pagination;
import com.bjpowernode.common.util.ResourceUtil;
import com.bjpowernode.common.util.SystemConstant;
import com.bjpowernode.system.entity.base.ResourceEntity;
import com.bjpowernode.system.entity.base.RoleEntity;
import com.bjpowernode.system.entity.base.UserEntity;
import com.bjpowernode.system.manager.ClientManager;
import com.bjpowernode.system.service.SystemService;
import com.bjpowernode.system.vo.Client;
import com.bjpowernode.system.vo.TreeNode;

@Controller
@RequestMapping("/loginController")
public class LoginController {

	private static final Logger logger = Logger.getLogger(LoginController.class);
	
	@Autowired
	private SystemService systemService;
	@Autowired
	private NoticeService noticeService;
	/**
	 * 登陆页
	 * @param error
	 * @param model
	 * @return
	 */
	@RequestMapping(params="login")
    public ModelAndView login(HttpServletRequest request){
		ModelAndView modelAndView = new ModelAndView("system/login");
		DetachedCriteria condition = DetachedCriteria.forClass(RoleEntity.class);
		Pagination<RoleEntity> pagination = systemService.getPageData(condition,1, 3);
		modelAndView.addObject("roleList", pagination.getDatas());
        return modelAndView;
    }
	
	@RequestMapping(params="home")
    public ModelAndView home(HttpServletRequest request){
		ModelAndView modelAndView = new ModelAndView("system/home");
		DetachedCriteria condition = DetachedCriteria.forClass(NoticeEntity.class);
		Pagination<NoticeEntity> pagination = noticeService.findPageData(condition,new NoticeEntity(),1, 20);
		modelAndView.addObject("noticeList", pagination.getDatas());
        return modelAndView;
    }
	/**
	 * 退出系统
	 * 
	 * @param user
	 * @param req
	 * @return
	 */
	@RequestMapping(params = "logout")
	public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = ContextHolderUtils.getSession();
		ClientManager.getInstance().removeClinet(session.getId());
		session.invalidate();
		ModelAndView modelAndView = new ModelAndView(new RedirectView(
				"loginController.do?login"));

		return modelAndView;
	}
	
	@RequestMapping(params="doLogin")
    public ModelAndView doLogin(HttpServletRequest req){
		ModelAndView mav = new ModelAndView("system/main");
		HttpSession session = ContextHolderUtils.getSession();
		Client client = ClientManager.getInstance().getClient(session.getId());
		req.setAttribute("username", client.getUser().getUsername());
        return mav;
    }
	
	@RequestMapping(params="doCheck")
	@ResponseBody
    public AjaxJson doCheck(HttpServletRequest req,String username, String password, String captcha, String roleType){
		HttpSession session = ContextHolderUtils.getSession();
		AjaxJson j = new AjaxJson();
		
		if(!captcha.equalsIgnoreCase(String.valueOf(session.getAttribute(SystemConstant.KEY_CAPTCHA)))){
			j.setSuccess(false);
			j.setMsg("验证码错误!");
		}else{
			UserEntity user = new UserEntity();
			user.setUsername(username);
			user.setPassword(password);
			user = this.systemService.getUserByNameAndPassword(user);
			if(user == null){
				j.setSuccess(false);
				j.setMsg("用户名或密码错误！");
				return j;
			}
			List<ResourceEntity> resourceList = new ArrayList<ResourceEntity>();
			List<RoleEntity> roleList = user.getRoles();
			for(RoleEntity re : roleList){
				
				//检查用户角色是否对应
				if(!re.getId().equals(roleType)){
					j.setSuccess(false);
					j.setMsg("用户类型选择错误！");
					return j;
				}
				
				List<ResourceEntity> tempRes = re.getResource();
				for(ResourceEntity res : tempRes){
					if(!resourceList.contains(res)){
						resourceList.add(res);
					}
				}
				
			}
			Client client = new Client();
            client.setIp(ResourceUtil.getIpAddr(req));
            client.setLogindatetime(new Date());
            client.setUser(user);
            client.setMenuList(resourceList);
            ClientManager.getInstance().addClinet(session.getId(),
                    client);
			if(user != null && user.getId() != null){
				if(user.getStatus() == 2){
					j.setSuccess(false);
					j.setMsg("该用户被禁用，请联系管理员！");
				}else{
					j.setSuccess(true);
					j.setMsg("登陆成功！");
				}
			}else{
				j.setSuccess(false);
				j.setMsg("用户名或密码错误!");
			}
		}
        return j;
    }
	
	
	@RequestMapping(params="getTreeMenu")
	@ResponseBody
	public String getTreeMenu(HttpServletRequest request){
		Client client = ResourceUtil.getClient();
		List<ResourceEntity> resourceList = new ArrayList<ResourceEntity>();
		if(client == null || client.getUser() == null){
			return "system/login";
		}else{
			resourceList = client.getMenuList();
		}
		
		List<ResourceEntity> resource = new ArrayList<ResourceEntity>();
		for(ResourceEntity re:resourceList){
			if(resourceList.size()<=0){
				break;
			}
			if(re.getParentResource() == null){
				resource.add(re);
			}
		}
		return JSONObject.valueToString(resourceToTreeNode(resource,resourceList));
	}
	
	/**
	 * 将sysResource类型的数据集合转化为前端较好识别的TreeNode
	 * @param resource
	 * @return
	 */
	private List<TreeNode> resourceToTreeNode(List<ResourceEntity> resource, List<ResourceEntity> userResource) {
		if (resource != null && !resource.isEmpty() && resource.get(0).getResourceType() == ResourceEntity.TYPE_MENU) {
			List<TreeNode> ch = new ArrayList<TreeNode>();
			for (ResourceEntity rr : resource) {
				TreeNode node = new TreeNode();
				if(userResource.contains(rr)){
					if(rr.getHref()==null){
						node.setId(rr.getId());
					}else{
						node.setId(rr.getId());
					}
					node.setId(rr.getId());
					node.setState("open");
					node.setText(rr.getName());
					
					Map<String, Object> _temp = new HashMap<String, Object>();
					_temp.put("href", rr.getHref());
					node.setAttributes(_temp);
					
					ch.add(node);
					
				}
				node.setChildren(resourceToTreeNode(rr.getResources(),userResource));
			}

			return ch;
		}
		return Collections.emptyList();
	}
}
