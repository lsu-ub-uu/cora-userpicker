/*
 * Copyright 2016 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.userpicker;

import java.util.List;

import se.uu.ub.cora.bookkeeper.data.DataGroup;

public final class UserInStorageUserPicker implements UserPicker {

	private UserStorage userStorage;
	private DataGroup dataGroupUser;
	private User user;

	private UserInStorageUserPicker(UserStorage userStorage) {
		this.userStorage = userStorage;
	}

	public static UserInStorageUserPicker usingUserStorage(UserStorage userStorage) {
		return new UserInStorageUserPicker(userStorage);
	}

	@Override
	public User pickGuest() {
		dataGroupUser = userStorage.getGuestUser();
		return convertDataGroupToUser();
	}

	private User convertDataGroupToUser() {
		createNewUserWithUserId();
		possiblyAddUserRoles();
		return user;
	}

	@Override
	public User pickUser(UserInfo userInfo) {
		return ensureActiveUserOrGuest(userInfo);
	}

	private User ensureActiveUserOrGuest(UserInfo userInfo) {
		try {
			return tryToGetActiveUserOrGuest(userInfo);
		} catch (Exception e) {
			return pickGuest();
		}
	}

	private User tryToGetActiveUserOrGuest(UserInfo userInfo) {
		if (null != userInfo.idInUserStorage) {
			tryToGetUserByStorageId(userInfo);
		} else {
			tryToGetUserByIdFromLogin(userInfo);
		}
		if (userIsActive()) {
			User pickedUser = convertDataGroupToUser();
			if (null != userInfo.idInUserStorage) {
				pickedUser.loginId = userInfo.idInUserStorage;
			} else {
				pickedUser.loginId = userInfo.idFromLogin;
			}
			if (dataGroupUser.containsChildWithNameInData("userFirstname")) {
				pickedUser.firstName = dataGroupUser.getFirstAtomicValueWithNameInData("userFirstname");
			}
			if (dataGroupUser.containsChildWithNameInData("userLastname")) {
				pickedUser.lastName = dataGroupUser.getFirstAtomicValueWithNameInData("userLastname");
			}
			return pickedUser;
		}
		return pickGuest();
	}

	private void tryToGetUserByStorageId(UserInfo userInfo) {
		dataGroupUser = userStorage.getUserById(userInfo.idInUserStorage);
	}

	private void tryToGetUserByIdFromLogin(UserInfo userInfo) {
		dataGroupUser = userStorage.getUserByIdFromLogin(userInfo.idFromLogin);
	}

	private void createNewUserWithUserId() {
		String id = extractUserIdFromUserFromStorage();
		user = new User(id);
	}

	private String extractUserIdFromUserFromStorage() {
		DataGroup recordInfo = dataGroupUser.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	private void possiblyAddUserRoles() {
		if (userIsActive()) {
			addUserRoleIdsToUserRoles();
		}
	}

	private boolean userIsActive() {
		String activeStatus = dataGroupUser.getFirstAtomicValueWithNameInData("activeStatus");
		return "active".equals(activeStatus);
	}

	private void addUserRoleIdsToUserRoles() {
		List<DataGroup> allGroupsWithNameInData = dataGroupUser.getAllGroupsWithNameInData("userRole");
		for (DataGroup extractedRole : allGroupsWithNameInData) {
			addUserRoleIdToUserRoles(extractedRole);
		}
	}

	private void addUserRoleIdToUserRoles(DataGroup extractedRole) {
		DataGroup extractedRoleLink = extractedRole.getFirstGroupWithNameInData("userRole");
		String roleId = extractedRoleLink.getFirstAtomicValueWithNameInData("linkedRecordId");
		user.roles.add(roleId);
	}
}
