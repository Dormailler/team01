package schedule;

import java.util.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import dto.ChatDTO;
import dto.GroupDTO;
import dto.GroupUserDTO;
import dto.MeetingScheduleDTO;
import dto.userScheduleDTO;

@Controller
public class scheduleController {
	
	@Autowired
	private scheduleService scheduleService;
	
	@RequestMapping(value = {"/schedule", "/schedule/"})
	public String start() throws Exception {

		return "schedule/scheduleError";
	}
	
	@RequestMapping(value = {"/schedule/{groupId}", "/schedule/{groupId}/"})
	public String start(@PathVariable("groupId") String groupId, Model model, HttpSession session, HttpServletRequest request) throws Exception {
		
		String userId = (String) session.getAttribute("session_id");
		if (userId == null) {
			return "schedule/schedule";
		}
		UserDTO user = scheduleService.selectUserOne(userId);
		model.addAttribute("username", user.getName());
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("groupId", groupId);
		
		List<UserDTO> userList = new ArrayList<UserDTO>();
		UserDTO userOne = new UserDTO();
		List<GroupDTO> groupList = new ArrayList<GroupDTO>();
		GroupDTO groupOne = new GroupDTO();	
		List<GroupUserDTO> groupUserList = new ArrayList<GroupUserDTO>();	
		GroupUserDTO groupUserOne = new GroupUserDTO();
		List<GroupUserDTO> groupUsers = new ArrayList<GroupUserDTO>();
		
		userList = scheduleService.selectUser();
		userOne = scheduleService.selectUserOne(userId);
		groupList = scheduleService.selectGroup();
		groupOne = scheduleService.selectGroupOne(groupId);
		groupUserList = scheduleService.selectGroupUser();
		groupUserOne = scheduleService.selectGroupUserOne(map);
		groupUsers = scheduleService.selectGroupUsers(groupId);
		
		String location = scheduleService.getLocation(groupId);
		List<ChatDTO> chatlist = scheduleService.getChat(groupId);	
		
		//그룹 정보 관련
		String groupName, groupCreateTime, ProjectEndTime, groupDescription;
		
		if (groupOne == null) {
			return "schedule/scheduleError";
			
		}else {
			groupName = groupOne.getGroup_name();
			
			groupCreateTime = groupOne.getGroup_create_time();
			groupCreateTime = groupCreateTime.split(" ")[0];
			
			ProjectEndTime = groupOne.getProject_end_time();
			if (ProjectEndTime != null)
				ProjectEndTime = ProjectEndTime.split(" ")[0];
			
			groupDescription = groupOne.getGroup_description();
		}
		
		//그룹 별 회원정보 관련
		List<String> groupUserUserIdList = new ArrayList<String>();
		List<String> WhoHostList = new ArrayList<String>();
		List<String> WhoSubHostList = new ArrayList<String>();
		List<String> WhoMemberList = new ArrayList<String>();
		List<String>WhoSetScheduleList = new ArrayList<String>();
		List<String>WhoDontSetScheduleList = new ArrayList<String>();
		
		if (groupUsers == null) {
			return "schedule/scheduleError";			
		}else {
			for(int i = 0; i < groupUsers.size(); i++) {
				
				groupUserUserIdList.add(groupUsers.get(i).getUser_id());
				
				if (groupUsers.get(i).getHost() == 1 && groupUsers.get(i).getSub_host() == 0) {
					WhoHostList.add(groupUsers.get(i).getUser_id());
				}
				if (groupUsers.get(i).getHost() == 0 && groupUsers.get(i).getSub_host() == 1) {
					WhoSubHostList.add(groupUsers.get(i).getUser_id());
				}
				if (groupUsers.get(i).getHost() == 0 && groupUsers.get(i).getSub_host() == 0) {
					WhoMemberList.add(groupUsers.get(i).getUser_id());
				}
				if (groupUsers.get(i).getSet_schedule() == 1) {
					WhoSetScheduleList.add(groupUsers.get(i).getUser_id());
				}else {
					WhoDontSetScheduleList.add(groupUsers.get(i).getUser_id());
				}
			}
		}
		
		//모든 그룹 내 유저를 대상으로
		String[] groupAllUsersId = new String[groupUserUserIdList.size()];
		String[] groupAllUsersName = new String[groupUserUserIdList.size()];
		String[] groupAllUsersAddress = new String[groupUserUserIdList.size()];
		String[] groupAllUsersPhone = new String[groupUserUserIdList.size()];
		String[] groupAllUsersEmail = new String[groupUserUserIdList.size()];
		String[] groupAllUsersProfileUrl = new String[groupUserUserIdList.size()];
		
		for (int i = 0; i < groupUserUserIdList.size(); i++) {
			groupAllUsersId[i] = scheduleService.selectUserOne(groupUserUserIdList.get(i)).getUser_id();
			groupAllUsersName[i] = scheduleService.selectUserOne(groupUserUserIdList.get(i)).getName();
			groupAllUsersAddress[i] = scheduleService.selectUserOne(groupUserUserIdList.get(i)).getAddress();
			groupAllUsersPhone[i] = scheduleService.selectUserOne(groupUserUserIdList.get(i)).getPhone();
			groupAllUsersEmail[i] = scheduleService.selectUserOne(groupUserUserIdList.get(i)).getEmail();
			groupAllUsersProfileUrl[i] = scheduleService.selectUserOne(groupUserUserIdList.get(i)).getProfile_url();
		}
		request.setAttribute("groupAllUsersId", groupAllUsersId);		
		request.setAttribute("groupAllUsersName", groupAllUsersName);
		request.setAttribute("groupAllUsersAddress", groupAllUsersAddress);
		request.setAttribute("groupAllUsersPhone", groupAllUsersPhone);
		request.setAttribute("groupAllUsersEmail", groupAllUsersEmail);
		request.setAttribute("groupAllUsersProfileUrl", groupAllUsersProfileUrl);
		
		//방장인 맴버를 대상으로
		String[] groupHostUserId = new String[WhoHostList.size()];
		String[] groupHostUserName = new String[WhoHostList.size()];
		String[] groupHostUserAddress = new String[WhoHostList.size()];
		String[] groupHostUserPhone = new String[WhoHostList.size()];
		String[] groupHostUserEmail = new String[WhoHostList.size()];
		String[] groupHostUserProfileUrl = new String[WhoHostList.size()];
		
		for (int i = 0; i < WhoHostList.size(); i++) {
			groupHostUserId[i] = scheduleService.selectUserOne(WhoHostList.get(i)).getUser_id();
			groupHostUserName[i] = scheduleService.selectUserOne(WhoHostList.get(i)).getName();
			groupHostUserAddress[i] = scheduleService.selectUserOne(WhoHostList.get(i)).getAddress();
			groupHostUserPhone[i] = scheduleService.selectUserOne(WhoHostList.get(i)).getPhone();
			groupHostUserEmail[i] = scheduleService.selectUserOne(WhoHostList.get(i)).getEmail();
			groupHostUserProfileUrl[i] = scheduleService.selectUserOne(WhoHostList.get(i)).getProfile_url();
		}
		request.setAttribute("groupHostUserId", groupHostUserId);		
		request.setAttribute("groupHostUserName", groupHostUserName);
		request.setAttribute("groupHostUserAddress", groupHostUserAddress);
		request.setAttribute("groupHostUserPhone", groupHostUserPhone);
		request.setAttribute("groupHostUserEmail", groupHostUserEmail);
		request.setAttribute("groupHostUserProfileUrl", groupHostUserProfileUrl);
		
		//부방장인 맴버를 대상으로
		String[] groupSubHostUsersId = new String[WhoSubHostList.size()];
		String[] groupSubHostUsersName = new String[WhoSubHostList.size()];
		String[] groupSubHostUsersAddress = new String[WhoSubHostList.size()];
		String[] groupSubHostUsersPhone = new String[WhoSubHostList.size()];
		String[] groupSubHostUsersEmail = new String[WhoSubHostList.size()];
		String[] groupSubHostUsersProfileUrl = new String[WhoSubHostList.size()];
		
		for (int i = 0; i < WhoSubHostList.size(); i++) {
			groupSubHostUsersId[i] = scheduleService.selectUserOne(WhoSubHostList.get(i)).getUser_id();
			groupSubHostUsersName[i] = scheduleService.selectUserOne(WhoSubHostList.get(i)).getName();
			groupSubHostUsersAddress[i] = scheduleService.selectUserOne(WhoSubHostList.get(i)).getAddress();
			groupSubHostUsersPhone[i] = scheduleService.selectUserOne(WhoSubHostList.get(i)).getPhone();
			groupSubHostUsersEmail[i] = scheduleService.selectUserOne(WhoSubHostList.get(i)).getEmail();
			groupSubHostUsersProfileUrl[i] = scheduleService.selectUserOne(WhoSubHostList.get(i)).getProfile_url();
		}
		request.setAttribute("groupSubHostUsersId", groupSubHostUsersId);		
		request.setAttribute("groupSubHostUsersName", groupSubHostUsersName);
		request.setAttribute("groupSubHostUsersAddress", groupSubHostUsersAddress);
		request.setAttribute("groupSubHostUsersPhone", groupSubHostUsersPhone);
		request.setAttribute("groupSubHostUsersEmail", groupSubHostUsersEmail);
		request.setAttribute("groupSubHostUsersProfileUrl", groupSubHostUsersProfileUrl);
		
		//일반맴버를 대상으로
		String[] groupMemberUsersId = new String[WhoMemberList.size()];
		String[] groupMemberUsersName = new String[WhoMemberList.size()];
		String[] groupMemberUsersAddress = new String[WhoMemberList.size()];
		String[] groupMemberUsersPhone = new String[WhoMemberList.size()];
		String[] groupMemberUsersEmail = new String[WhoMemberList.size()];
		String[] groupMemberUsersProfileUrl = new String[WhoMemberList.size()];
		
		for (int i = 0; i < WhoMemberList.size(); i++) {
			groupMemberUsersId[i] = scheduleService.selectUserOne(WhoMemberList.get(i)).getUser_id();
			groupMemberUsersName[i] = scheduleService.selectUserOne(WhoMemberList.get(i)).getName();
			groupMemberUsersAddress[i] = scheduleService.selectUserOne(WhoMemberList.get(i)).getAddress();
			groupMemberUsersPhone[i] = scheduleService.selectUserOne(WhoMemberList.get(i)).getPhone();
			groupMemberUsersEmail[i] = scheduleService.selectUserOne(WhoMemberList.get(i)).getEmail();
			groupMemberUsersProfileUrl[i] = scheduleService.selectUserOne(WhoMemberList.get(i)).getProfile_url();
		}
		request.setAttribute("groupMemberUsersId", groupMemberUsersId);		
		request.setAttribute("groupMemberUsersName", groupMemberUsersName);
		request.setAttribute("groupMemberUsersAddress", groupMemberUsersAddress);
		request.setAttribute("groupMemberUsersPhone", groupMemberUsersPhone);
		request.setAttribute("groupMemberUsersEmail", groupMemberUsersEmail);
		request.setAttribute("groupMemberUsersProfileUrl", groupMemberUsersProfileUrl);
		
		//스케쥴을 입력한 맴버를 대상으로
		String[] groupSetScheduleUsersId = new String[WhoSetScheduleList.size()];
		String[] groupSetScheduleUsersName = new String[WhoSetScheduleList.size()];
		String[] groupSetScheduleUsersAddress = new String[WhoSetScheduleList.size()];
		String[] groupSetScheduleUsersPhone = new String[WhoSetScheduleList.size()];
		String[] groupSetScheduleUsersEmail = new String[WhoSetScheduleList.size()];
		String[] groupSetScheduleUsersProfileUrl = new String[WhoSetScheduleList.size()];
		
		for (int i = 0; i < WhoSetScheduleList.size(); i++) {
			groupSetScheduleUsersId[i] = scheduleService.selectUserOne(WhoSetScheduleList.get(i)).getUser_id();
			groupSetScheduleUsersName[i] = scheduleService.selectUserOne(WhoSetScheduleList.get(i)).getName();
			groupSetScheduleUsersAddress[i] = scheduleService.selectUserOne(WhoSetScheduleList.get(i)).getAddress();
			groupSetScheduleUsersPhone[i] = scheduleService.selectUserOne(WhoSetScheduleList.get(i)).getPhone();
			groupSetScheduleUsersEmail[i] = scheduleService.selectUserOne(WhoSetScheduleList.get(i)).getEmail();
			groupSetScheduleUsersProfileUrl[i] = scheduleService.selectUserOne(WhoSetScheduleList.get(i)).getProfile_url();
		}
		request.setAttribute("groupSetScheduleUsersId", groupSetScheduleUsersId);		
		request.setAttribute("groupSetScheduleUsersName", groupSetScheduleUsersName);
		request.setAttribute("groupSetScheduleUsersAddress", groupSetScheduleUsersAddress);
		request.setAttribute("groupSetScheduleUsersPhone", groupSetScheduleUsersPhone);
		request.setAttribute("groupSetScheduleUsersEmail", groupSetScheduleUsersEmail);
		request.setAttribute("groupSetScheduleUsersProfileUrl", groupSetScheduleUsersProfileUrl);
		
		//스케쥴을 입력하지 않은 맴버를 대상으로
		String[] groupDontSetScheduleUsersId = new String[WhoDontSetScheduleList.size()];
		String[] groupDontSetScheduleUsersName = new String[WhoDontSetScheduleList.size()];
		String[] groupDontSetScheduleUsersAddress = new String[WhoDontSetScheduleList.size()];
		String[] groupDontSetScheduleUsersPhone = new String[WhoDontSetScheduleList.size()];
		String[] groupDontSetScheduleUsersEmail = new String[WhoDontSetScheduleList.size()];
		String[] groupDontSetScheduleUsersProfileUrl = new String[WhoDontSetScheduleList.size()];
		
		for (int i = 0; i < WhoDontSetScheduleList.size(); i++) {
			groupDontSetScheduleUsersId[i] = scheduleService.selectUserOne(WhoDontSetScheduleList.get(i)).getUser_id();
			groupDontSetScheduleUsersName[i] = scheduleService.selectUserOne(WhoDontSetScheduleList.get(i)).getName();
			groupDontSetScheduleUsersAddress[i] = scheduleService.selectUserOne(WhoDontSetScheduleList.get(i)).getAddress();
			groupDontSetScheduleUsersPhone[i] = scheduleService.selectUserOne(WhoDontSetScheduleList.get(i)).getPhone();
			groupDontSetScheduleUsersEmail[i] = scheduleService.selectUserOne(WhoDontSetScheduleList.get(i)).getEmail();
			groupDontSetScheduleUsersProfileUrl[i] = scheduleService.selectUserOne(WhoDontSetScheduleList.get(i)).getProfile_url();
		}
		request.setAttribute("groupDontSetScheduleUsersId", groupDontSetScheduleUsersId);		
		request.setAttribute("groupDontSetScheduleUsersName", groupDontSetScheduleUsersName);
		request.setAttribute("groupDontSetScheduleUsersAddress", groupDontSetScheduleUsersAddress);
		request.setAttribute("groupDontSetScheduleUsersPhone", groupDontSetScheduleUsersPhone);
		request.setAttribute("groupDontSetScheduleUsersEmail", groupDontSetScheduleUsersEmail);
		request.setAttribute("groupDontSetScheduleUsersProfileUrl", groupDontSetScheduleUsersProfileUrl);		
		
		//오늘 날짜와 1주일 후 날짜
		Date today = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy/MM/dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		cal.add(Calendar.DATE, 6);
		String startDate = format.format(today);
		String endDate = format.format(cal.getTime());
		
		//그룹 별 일정표 데이터	
		List<MeetingScheduleDTO> tableList = new ArrayList<MeetingScheduleDTO>();
		map.put("group_id", groupId);
		map.put("showView", 1);
		int tableListCnt = 1;	
		tableList= scheduleService.selectMeetingScheduleAllShow(map);
		if (scheduleService.selectMeetingScheduleAllShowCnt(map) >0) {
			tableListCnt = scheduleService.selectMeetingScheduleAllShowCnt(map);
			
			String[] tableListStart = new String[tableListCnt];
			String[] tableListLast = new String[tableListCnt];
			for (int i = 0; i < tableList.size(); i++) {
				String[] temp = new String[3];
				temp = tableList.get(i).getFirst_date().split("-");
				tableListStart[i] = temp[0] + "/" + temp[1] + "/" + temp[2];
				temp = tableList.get(i).getLast_date().split("-");
				tableListLast[i] = temp[0] + "/" + temp[1] + "/" + temp[2];
			}
			model.addAttribute("tableListLast", tableListLast);
			model.addAttribute("tableListStart", tableListStart);
		}else {
			String[] tableListStart = new String[1];
			String[] tableListLast = new String[1];
			tableListStart[0] = format2.format(today);
			tableListLast[0] = format2.format(cal.getTime());
			request.setAttribute("tableListLast", tableListLast);
			request.setAttribute("tableListStart", tableListStart);
		}
		request.setAttribute("tableListCnt", tableListCnt);
		
		//그룹 당 설정된 날짜
		if(scheduleService.selectMeetingScheduleDateCnt(groupId) > 0) {
			startDate 
			= scheduleService.selectMeetingScheduleDate(groupId).getSet_date1();
			endDate 
			= scheduleService.selectMeetingScheduleDate(groupId).getSet_date2();
		}
		request.setAttribute("startDate", startDate);
		request.setAttribute("endDate", endDate);
		
		//유저 별 일정 테이블 표시
		List<MeetingScheduleDTO> msdto = new ArrayList<MeetingScheduleDTO>();
		map.put("group_id", groupId);
		map.put("showView", 1);
		msdto = scheduleService.selectMeetingScheduleAllShow(map);
		int [] seqs = new int [msdto.size()];
		for (int i = 0; i < msdto.size(); i++) {
			seqs[i] = msdto.get(i).getSeq();
		}
		
		userScheduleDTO usdto = new userScheduleDTO();
		usdto.setGroup_id(groupId);
		HashMap<String, Object> usmap = new HashMap<String, Object>();		
		//sunday의 배열 : 이중배열. [seq][cnt] = 입력된 갯수
		int[][] SundayList = new int[seqs.length][42]; 
		int[][] MondayList = new int[seqs.length][42]; 
		int[][] TuesdayList = new int[seqs.length][42]; 
		int[][] WednesdayList = new int[seqs.length][42]; 
		int[][] ThusdayList = new int[seqs.length][42]; 
		int[][] FridayList = new int[seqs.length][42]; 
		int[][] SaturdayList = new int[seqs.length][42]; 

			/*group_id / seq / count 가 일치할 때, sun~sat에 1인 값의 총 개수 합
			이 배열의 크기는 group_id/seq/count의 개수만큼 존재한다.*/
			usmap.put("group_id", groupId);
			usmap.put("sun", 0); usmap.put("mon", 0); usmap.put("tue", 0); usmap.put("wed", 0);
			usmap.put("thu", 0); usmap.put("fri", 0); usmap.put("sat", 0);
			for (int i = 0; i < seqs.length; i++) {
				usmap.put("seq", seqs[i]);
				for (int j = 0; j < 42; j++) {
					if (scheduleService.selectUserScheduleCntAll(usdto) > 0) {
						usmap.put("cnt", j);
						usmap.put("sun", 1);
						SundayList[i][j] = scheduleService.selectUserScheduleDayCnt(usmap);
						usmap.put("sun", 0);
						usmap.put("mon", 1);
						MondayList[i][j] = scheduleService.selectUserScheduleDayCnt(usmap);
						usmap.put("mon", 0);
						usmap.put("tue", 1);
						TuesdayList[i][j] = scheduleService.selectUserScheduleDayCnt(usmap);
						usmap.put("tue", 0);
						usmap.put("wed", 1);
						WednesdayList[i][j] = scheduleService.selectUserScheduleDayCnt(usmap);
						usmap.put("wed", 0);
						usmap.put("thu", 1);
						ThusdayList[i][j] = scheduleService.selectUserScheduleDayCnt(usmap);
						usmap.put("thu", 0);
						usmap.put("fri", 1);
						FridayList[i][j] = scheduleService.selectUserScheduleDayCnt(usmap);
						usmap.put("fri", 0);
						usmap.put("sat", 1);
						SaturdayList[i][j] = scheduleService.selectUserScheduleDayCnt(usmap);
						usmap.put("sat", 0);
					}else {
						SundayList[i][j] = 0;
						MondayList[i][j] = 0;
						TuesdayList[i][j] = 0;
						WednesdayList[i][j] = 0;
						ThusdayList[i][j] = 0;
						FridayList[i][j] = 0;
						SaturdayList[i][j] = 0;
					}
				}
			}
			request.setAttribute("SundayList", SundayList);
			request.setAttribute("MondayList", MondayList);
			request.setAttribute("TuesdayList", TuesdayList);
			request.setAttribute("WednesdayList", WednesdayList);
			request.setAttribute("ThusdayList", ThusdayList);
			request.setAttribute("FridayList", FridayList);
			request.setAttribute("SaturdayList", SaturdayList);

		//유저 별 일정표 수정 표시 (이전에 입력했던 거 보이도록)
		usdto.setGroup_id(groupId);
		usdto.setUser_id(userId);
		//sunday의 배열 : 이중배열. [seq][cnt] = 입력된 갯수
		int[][] SundayList2 = new int[seqs.length][42]; 
		int[][] MondayList2 = new int[seqs.length][42]; 
		int[][] TuesdayList2 = new int[seqs.length][42]; 
		int[][] WednesdayList2 = new int[seqs.length][42]; 
		int[][] ThusdayList2 = new int[seqs.length][42]; 
		int[][] FridayList2 = new int[seqs.length][42]; 
		int[][] SaturdayList2 = new int[seqs.length][42]; 
			/*group_id / seq / count 가 일치할 때, sun~sat에 1인 값
			이 배열의 크기는 group_id/seq/count의 개수만큼 존재한다.*/
			for (int i = 0; i < seqs.length; i++) {
				usdto.setSeq(seqs[i]);
				for (int j = 0; j < 42; j++) {
					usdto.setCnt(j);
					if (scheduleService.selectUserScheduleCnt(usdto) > 0) {
						SundayList2[i][j] = scheduleService.selectUserSchedule(usdto).getSun();
						MondayList2[i][j] = scheduleService.selectUserSchedule(usdto).getMon();
						TuesdayList2[i][j] = scheduleService.selectUserSchedule(usdto).getTue();
						WednesdayList2[i][j] = scheduleService.selectUserSchedule(usdto).getWed();
						ThusdayList2[i][j] = scheduleService.selectUserSchedule(usdto).getThu();
						FridayList2[i][j] = scheduleService.selectUserSchedule(usdto).getFri();
						SaturdayList2[i][j] = scheduleService.selectUserSchedule(usdto).getSat();
					}else {
						SundayList2[i][j] = 0;
						MondayList2[i][j] = 0;
						TuesdayList2[i][j] = 0;
						WednesdayList2[i][j] = 0;
						ThusdayList2[i][j] = 0;
						FridayList2[i][j] = 0;
						SaturdayList2[i][j] = 0;
					}
				}
			}
			request.setAttribute("SundayList2", SundayList2);
			request.setAttribute("MondayList2", MondayList2);
			request.setAttribute("TuesdayList2", TuesdayList2);
			request.setAttribute("WednesdayList2", WednesdayList2);
			request.setAttribute("ThusdayList2", ThusdayList2);
			request.setAttribute("FridayList2", FridayList2);
			request.setAttribute("SaturdayList2", SaturdayList2);
			
		//D-day 표시
		String final_schedule_str = scheduleService.selectGroupOne(groupId).getFinal_schedule();
		String[] finalScheduleList;
		if (final_schedule_str != null) {
			finalScheduleList = final_schedule_str.split(",");
			request.setAttribute("DdayTrue", 1);
		}else {
			finalScheduleList = new String[4];
			finalScheduleList[0] = "-";
			finalScheduleList[1] = "미정";
			finalScheduleList[2] = "미정";
			finalScheduleList[3] = "*";
			request.setAttribute("DdayTrue", 0);
		}
		model.addAttribute("finalScheduleList", finalScheduleList);
		
		request.setAttribute("userId", userId);
		
		model.addAttribute("userId", userId);		
		model.addAttribute("groupName", groupName);
		model.addAttribute("groupCreateTime", groupCreateTime);
		model.addAttribute("ProjectEndTime", ProjectEndTime);
		model.addAttribute("groupDescription", groupDescription);
		model.addAttribute("location", location);
		model.addAttribute("groupId", groupId);
		model.addAttribute("chatlist", chatlist);
		
		return "schedule/schedule";
	}
	
	@RequestMapping("/schedule/{groupId}/update")
	public String updateSubHost(@PathVariable("groupId") String groupId, 
			String userId, 
			String subHost,
			Model model) throws Exception {
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		int subHostInt = Integer.parseInt(subHost);
		map.put("sub_host", subHostInt);
		map.put("group_id", groupId);
		map.put("user_id", userId);
		scheduleService.updateGroupUserSubHost(map);
		
		return "redirect:";
	}
	
	@RequestMapping("/schedule/{groupId}/tableUpdate")
	public String updateTable(@PathVariable("groupId") String groupId, 
			@RequestParam("start") String start,
			@RequestParam("end") String end,
			@RequestParam("data") String data,
			HttpServletRequest request) throws Exception {
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("set_date1", start);
		map.put("set_date2", end);
		map.put("group_id", groupId);
		if(scheduleService.selectMeetingScheduleDateCnt(groupId) > 0) {
			scheduleService.updateMeetingScheduleDate(map);
		}else {
			scheduleService.insertMeetingScheduleDate(map);
		}
		String[] datas = data.split("\\*");
		
		String startWeekFirst = datas[0];
		String startWeekLast = datas[1];
		String WeeksCnt = datas[2];
		int WeeksCntInt = Integer.parseInt(WeeksCnt);
		
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		
		cal1.set(Integer.parseInt(startWeekFirst.substring(0, 4)), 
				Integer.parseInt(startWeekFirst.substring(4, 6))-1, 
				Integer.parseInt(startWeekFirst.substring(6)));
		
		cal2.set(Integer.parseInt(startWeekLast.substring(0, 4)), 
				Integer.parseInt(startWeekLast.substring(4, 6))-1, 
				Integer.parseInt(startWeekLast.substring(6)));
		
		Date date1 = new Date();
		Date date2 = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		
		date1 = cal1.getTime();
		date2 = cal2.getTime();
		
		//그룹 생성일 (seq)
		Calendar cal3 = Calendar.getInstance();
				
		String createTime  = scheduleService.selectGroupOne(groupId).getGroup_create_time();
		createTime = createTime.split(" ")[0];
		int createTimeY = Integer.parseInt(createTime.split("-")[0]);
		int createTimeM = Integer.parseInt(createTime.split("-")[1]);
		int createTimeD = Integer.parseInt(createTime.split("-")[2]);
				
		cal3.set(createTimeY, createTimeM-1, createTimeD);
		cal3.add(Calendar.DATE, -cal3.get(Calendar.DAY_OF_WEEK)+1);	//일요일
				
		Date date3 = new Date();
		date3 = cal3.getTime();
		
		int [] seqList = new int[WeeksCntInt];		
		String [] startWeekFirstList = new String[WeeksCntInt];
		String [] startWeekLastList = new String[WeeksCntInt];
		
		//시트가 중복으로 저장되는 오류가 있었는데 어떤 조건에서 나는지 못찾음...
		for (int i = 0; i < WeeksCntInt; i++) {
			System.out.println("첫 날 : " + date1);
			System.out.println("마지막 날 : " + date3);
			long diffDays = (date1.getTime()-date3.getTime())/(24*60*60*1000);
			int diffDay = Long.valueOf(diffDays).intValue();
			System.out.println("날짜 차이 : " + diffDay);
			diffDay /= 7;
			System.out.println("주 차이 : " + diffDay);
			seqList[i] = diffDay;
			startWeekFirstList[i] = format.format(date1);
			startWeekLastList[i] = format.format(date2);
			System.out.println("입력일1 : " + startWeekFirstList[i]);
			System.out.println("입력일2 : " + startWeekLastList[i]);
			cal1.add( Calendar.DATE , 7);
			cal2.add( Calendar.DATE , 7);
			date1 = cal1.getTime();
			date2 = cal2.getTime();
		}
		
		MeetingScheduleDTO dto = new MeetingScheduleDTO();
		
		scheduleService.updateMeetingScheduleShowViewAllZero(groupId);
		
		for (int i = 0; i < WeeksCntInt; i++) {
			map.put("group_id", groupId);
			map.put("seq", seqList[i]);
			
			dto.setFirst_date(startWeekFirstList[i]);
			dto.setGroup_id(groupId);
			dto.setLast_date(startWeekLastList[i]);
			dto.setSeq(seqList[i]);
			dto.setShowView(1);
			
			if (scheduleService.selectMeetingScheduleCnt(map) != 0) {
				scheduleService.updateMeetingScheduleShowViewOne(map);
				
			}else {
				scheduleService.insertMeetingSchedule(dto);
			}
		}		
		
		return "redirect:";
	}
	
	@RequestMapping("/schedule/{groupId}/addchat")
	@ResponseBody
	public void addchat(@PathVariable("groupId") String groupId, String userId, String text) throws Exception {
		HashMap<String, Object> map = new HashMap<String, Object>();
		UserDTO userOne = scheduleService.selectUserOne(userId);
		System.out.println(userId);
		LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd HH:mm");

        String formattedTime = now.format(formatter);
        int hour = Integer.parseInt(formattedTime.substring(6,8));
        String time = "오전 ";
        if(hour > 12) {
        	hour = hour - 12;
        	time = "오후 ";
        }
        String now_time = formattedTime.substring(0,6) + time + hour + formattedTime.substring(8,11);

		map.put("group_id", groupId);
		map.put("name", userOne.getName());
		map.put("text", text);
		map.put("profile_url", userOne.getProfile_url());
		map.put("now", now_time);
		scheduleService.addCaht(map);
	}
	
	
	@RequestMapping("/schedule/{groupId}/updateUserTable")
	public String updateUserTable(@PathVariable("groupId") String groupId,
			@RequestParam("code") String code,
			HttpSession session) throws Exception {
		
		String userId = (String)session.getAttribute("session_id");
		
		userScheduleDTO dto = new userScheduleDTO();
		dto.setGroup_id(groupId);
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("group_id", groupId);
		map.put("showView", 1);
		
		List<MeetingScheduleDTO> msdto = scheduleService.selectMeetingScheduleAllShow(map);
		int [] seqs = new int[msdto.size()];
	
		for (int i = 0; i < msdto.size(); i++) {
			seqs[i] = msdto.get(i).getSeq();
		}
		//update하면 code 마지막에 쉼표(,)가 붙는다. 이유가 뭐지
		code = code.split(",")[0];
		String[] sheets = code.split("a");
		
		String[][] datas = new String [sheets.length][42];
		for (int i = 0; i < sheets.length; i++) {
			for (int j = 0; j < 42; j++) {
				datas[i][j] = sheets[i].split("b")[j];
				
				dto.setSeq(seqs[i]);
				dto.setCnt(j);
				dto.setUser_id(userId);
				dto.setSun(datas[i][j].charAt(0)-48);
				dto.setMon(datas[i][j].charAt(1)-48);
				dto.setTue(datas[i][j].charAt(2)-48);
				dto.setWed(datas[i][j].charAt(3)-48);
				dto.setThu(datas[i][j].charAt(4)-48);
				dto.setFri(datas[i][j].charAt(5)-48);
				dto.setSat(datas[i][j].charAt(6)-48);
				
				if (scheduleService.selectUserScheduleCnt(dto) > 0) {
					//update
					scheduleService.updateUserSchedule(dto);
					map.put("user_id", userId);
					map.put("group_id", groupId);
					map.put("set_schedule", 1);
					scheduleService.updateGroupUserSetSchedule(map);
				}else {
					//insert
					scheduleService.insertUserSchedule(dto);
					map.put("user_id", userId);
					map.put("group_id", groupId);
					map.put("set_schedule", 1);
					scheduleService.updateGroupUserSetSchedule(map);
				}
			}
		}
		
		return "redirect:";
	}
	
	@RequestMapping("/schedule/{groupId}/updateDday")
	public String updateDday(@PathVariable("groupId") String groupId,
			@RequestParam(value = "date", required = false) String date,
			@RequestParam(value = "start", required = false) String start,
			@RequestParam(value = "end", required = false) String end) throws Exception {
		
		if (date == null || date == "") {
			return "redirect:";
		}
		if (start == null || start == "") {
			start = "미정";
		}
		if (end == null || end == "") {
			end = "미정";
		}
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		Date today = new Date();
		Date date2 = new Date();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 (E)");
		
		String[] dates = date.split("-");
		cal.setTime(date2);
		
		cal.set(Integer.parseInt(dates[0]), Integer.parseInt(dates[1])-1, Integer.parseInt(dates[2]));		
		date2= cal.getTime();
		
		int dday = (int) (Math.ceil(Math.abs((date2.getTime() - today.getTime())/(1000*60*60*24) )));
		
		String resultDday = dday+"";
		if (dday == 0) {
			resultDday = "Today";
		}
		else if (dday < 0) {
			resultDday = "+ " + resultDday;
		}
		
		String resultDate = format.format(date2);
		resultDate += "," + start + "," + end + "," + resultDday;
		
		map.put("group_id", groupId);
		map.put("final_schedule", resultDate);
		
		scheduleService.updateGroupSchedule(map);
		return "redirect:";
	}
}
