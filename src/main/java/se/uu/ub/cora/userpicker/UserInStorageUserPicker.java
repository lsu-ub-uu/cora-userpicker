/*
 * Copyright 2016, 2018, 2022 Uppsala University Library
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

import se.uu.ub.cora.gatekeeper.picker.UserInfo;
import se.uu.ub.cora.gatekeeper.picker.UserPicker;
import se.uu.ub.cora.gatekeeper.storage.UserStorageView;
import se.uu.ub.cora.gatekeeper.user.User;

public final class UserInStorageUserPicker implements UserPicker {
	private UserStorageView userStorage;
	private String guestUserId;

	private UserInStorageUserPicker(UserStorageView userStorage, String guestUserId) {
		this.userStorage = userStorage;
		this.guestUserId = guestUserId;
	}

	public static UserInStorageUserPicker usingUserStorageAndGuestUserId(
			UserStorageView userStorage, String guestUserId) {
		return new UserInStorageUserPicker(userStorage, guestUserId);
	}

	@Override
	public User pickGuest() {
		return userStorage.getUserById(guestUserId);
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
		User userFromStorage = getUserFromStorage(userInfo);
		if (userFromStorage.active) {
			return userFromStorage;
		}
		return pickGuest();
	}

	private User getUserFromStorage(UserInfo userInfo) {
		if (null != userInfo.idInUserStorage) {
			return getUserFromStorageBasedOnId(userInfo);
		}
		return getUserFromStorageBasedOnLoginId(userInfo);
	}

	private User getUserFromStorageBasedOnId(UserInfo userInfo) {
		return userStorage.getUserById(userInfo.idInUserStorage);
	}

	private User getUserFromStorageBasedOnLoginId(UserInfo userInfo) {
		return userStorage.getUserByLoginId(userInfo.loginId);
	}

	public UserStorageView onlyForTestGetUserStorage() {
		return userStorage;
	}

	public String onlyForTestGetCurrentGuestUserId() {
		return guestUserId;
	}

}
