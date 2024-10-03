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
 *     You should have received DEFAULT_GUEST_USERIDa copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.userpicker;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.gatekeeper.picker.UserInfo;
import se.uu.ub.cora.gatekeeper.storage.UserStorageViewException;
import se.uu.ub.cora.gatekeeper.user.User;

public class UserInStorageUserPickerTest {
	private static final String USER_ID = "someUserId";
	private static final String LOGIN_ID = "someLoginId";
	private static final String GUEST_ID = "12345";
	private UserInStorageUserPicker userPicker;

	private UserStorageViewSpy userStorageView;

	@BeforeMethod
	public void setUp() {
		userStorageView = new UserStorageViewSpy();
		userPicker = UserInStorageUserPicker.usingUserStorageAndGuestUserId(userStorageView,
				GUEST_ID);

		User userFromStorage = createActiveUserUsingUserIdAndLoginId(USER_ID, LOGIN_ID);

		userStorageView.MRV.setDefaultReturnValuesSupplier("getUserById", () -> userFromStorage);
		userStorageView.MRV.setDefaultReturnValuesSupplier("getUserByLoginId",
				() -> userFromStorage);
	}

	private User createActiveUserUsingUserIdAndLoginId(String userId, String loginId) {
		User userFromStorage = new User(userId);
		userFromStorage.loginId = loginId;
		userFromStorage.active = true;
		return userFromStorage;
	}

	@Test
	public void testGuest() {
		User user = userPicker.pickGuest();

		userStorageView.MCR.assertParameters("getUserById", 0, GUEST_ID);
		userStorageView.MCR.assertReturn("getUserById", 0, user);
	}

	@Test
	public void testGetUserStorage() throws Exception {
		assertEquals(userPicker.onlyForTestGetUserStorage(), userStorageView);
	}

	@Test
	public void testGetCurrentUserId() throws Exception {
		assertEquals(userPicker.onlyForTestGetCurrentGuestUserId(), GUEST_ID);
	}

	@Test
	public void testPickUser() {
		User user = pickUserUsingIdInStorage("aUserId");

		assertEquals(user.loginId, LOGIN_ID);
		userStorageView.MCR.assertParameters("getUserById", 0, "aUserId");
		userStorageView.MCR.assertReturn("getUserById", 0, user);
		userStorageView.MCR.assertNumberOfCallsToMethod("getUserById", 1);
	}

	@Test
	public void testUnknownUserIsGuest() {
		userStorageView.MRV.setThrowException("getUserById",
				UserStorageViewException.usingMessage("error from spy"), "unknownUser");

		User user = pickUserUsingIdInStorage("unknownUser");

		userStorageView.MCR.assertParameters("getUserById", 0, "unknownUser");
		userStorageView.MCR.assertParameters("getUserById", 1, GUEST_ID);
		userStorageView.MCR.assertReturn("getUserById", 0, user);
	}

	private User pickUserUsingIdInStorage(String idInStorage) {
		UserInfo userInfo = UserInfo.withIdInUserStorage(idInStorage);
		return userPicker.pickUser(userInfo);
	}

	@Test
	public void testInactiveUserReturnsGuest() {
		User userToReturnFromStorage = new User(USER_ID);
		userToReturnFromStorage.active = false;

		userStorageView.MRV.setReturnValues("getUserById", List.of(userToReturnFromStorage),
				"anyUserId");

		User user = pickUserUsingIdInStorage("anyUserId");

		userStorageView.MCR.assertParameters("getUserById", 0, "anyUserId");
		userStorageView.MCR.assertParameters("getUserById", 1, GUEST_ID);
		userStorageView.MCR.assertReturn("getUserById", 1, user);
	}

	private User pickUserUsingLoginId(String loginId) {
		UserInfo userInfo = UserInfo.withLoginId(loginId);
		User user = userPicker.pickUser(userInfo);
		return user;
	}

	@Test
	public void testPickUser_loginId() {
		User user = pickUserUsingLoginId(LOGIN_ID);

		assertEquals(user.loginId, LOGIN_ID);
		userStorageView.MCR.assertParameters("getUserByLoginId", 0, LOGIN_ID);
		userStorageView.MCR.assertReturn("getUserByLoginId", 0, user);
		userStorageView.MCR.assertNumberOfCallsToMethod("getUserByLoginId", 1);
	}

	@Test
	public void testUnknownUserIsGuest_loginId() {
		userStorageView.MRV.setThrowException("getUserByLoginId",
				UserStorageViewException.usingMessage("error from spy"), "fitnesse@ub.uu.se");

		User user = pickUserUsingLoginId("fitnesse@ub.uu.se");

		userStorageView.MCR.assertParameters("getUserByLoginId", 0, "fitnesse@ub.uu.se");
		userStorageView.MCR.assertParameters("getUserById", 0, GUEST_ID);
		userStorageView.MCR.assertReturn("getUserById", 0, user);
	}

	@Test
	public void testInactiveUserReturnsGuest_loginId() {
		User userToReturnFromStorage = new User(USER_ID);
		userToReturnFromStorage.active = false;

		userStorageView.MRV.setReturnValues("getUserByLoginId", List.of(userToReturnFromStorage),
				"fitnesse@ub.uu.se");

		User user = pickUserUsingLoginId("fitnesse@ub.uu.se");

		userStorageView.MCR.assertParameters("getUserByLoginId", 0, "fitnesse@ub.uu.se");
		userStorageView.MCR.assertParameters("getUserById", 0, GUEST_ID);
		userStorageView.MCR.assertReturn("getUserById", 0, user);
	}

}
