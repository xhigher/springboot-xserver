# springboot-xserver
基于springboot构建的微服务框架最佳实践，支持多数据源的数据库和缓存

简介
---
  基于springboot构建的微服务框架，resful API风格，json数据格式交互，核心逻辑简洁易懂，定制灵活；mysql，redis，mongodb，elasticsearch等即开即用，资源管理完全托管。
  业务逻辑层只需专注于业务流程实现，数据层无缝支持多数据源；打包部署简单，，方便本地测试和服务上线，通过MAVEN工具能够打出各个环境（根据指定配置文件）安装包；



接口层
```java
//定义控制层
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "user")
public class UserController extends XController {


	//注入业务服务实例
    @Bean
    private XLogic info() {
    	return this.newLogic(Info.class);
    }
	
	//接口定义
    @RequestMapping(value = "info", method = RequestMethod.GET)
    public String getInfo(HttpServletRequest request) throws IOException {
        return executeLogic(Info.class, request);
    }
    
    //注入业务服务实例
    @Bean
    private XLogic update() {
    	return this.newLogic(Update.class);
    }

    //接口定义
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public String updateInfo(HttpServletRequest request) throws IOException {
    	return executeLogic(Update.class, request);
    }
}
```

业务层
```java
	//注解：接口版本（扩展同名接口），必要参数，限定ip等
	@XLogicConfig(requiredParameters={DataKey.USERID, DataKey.NICKNAME, DataKey.AVATAR})
	public final class Update extends XLogic {

	    @Override
	    protected boolean requireSession() {
	        return false;
	    }

	    private String userid;
		private String nickname;
	    private String avatar;

	    @Override
	    protected String prepare() {
	    	userid = this.getString(DataKey.USERID);
	        if (userid.length() != 12) {
	            return XLogicResult.errorParameter("USERID_ERROR");
	        }
	    	nickname = this.getString(DataKey.NICKNAME);
	        if (nickname.length() < 2 || nickname.length()>100) {
	            return XLogicResult.errorParameter("NICKNAME_ERROR");
	        }
	        avatar = this.getString(DataKey.AVATAR);
	        if (avatar.isEmpty()) {
	            return XLogicResult.errorParameter("AVATAR_ERROR");
	        }
	        return null;
	    }

		@Override
		protected String execute() {
			UserInfoModel userModel = new UserInfoModel();
			if(!userModel.updateInfo(userid, nickname, avatar)) {
				return XLogicResult.error();
			}
			return XLogicResult.success();
		}

	}

```

数据层

```java
 	//数据库

	public abstract class BusinessDatabase extends XModel {

		//指定配置数据源
		public static final String dataSourceName = "business";
		
		@Override
		protected String getDataSourceName() {
			return dataSourceName;
		}
		
	}

 	//业务表
 	//继承指定数据源的基类
	public class UserInfoModel extends BusinessDatabase {

		//指定库表名称
		@Override
		protected String tableName() {
			return "user_info";
		}

		//通过账号名获取用户信息
		public JSONObject getInfoByUsername(String username){
			return this.prepare().addWhere("username",username).find();
		}

		//通过用户ID获取用户信息
		public JSONObject getInfo(String userid){
			return this.prepare().addWhere("userid",userid).find();
		}
		
		//更新用户信息
		public boolean updateInfo(String userid, String nickname, String avatar){
			Map<String, Object> values = new HashMap<String, Object>();
			values.put("nickname", nickname);
			values.put("avatar", avatar);
			values.put("updatetime", CommonUtils.getCurrentYMDHMS());
			return this.prepare().set(values).addWhere("userid", userid).update();
		}
	}
```






