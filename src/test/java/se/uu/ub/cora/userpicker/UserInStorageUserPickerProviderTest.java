/*
 * Copyright 2019 Olov McKie
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.gatekeeper.user.UserPickerProvider;

public class UserInStorageUserPickerProviderTest {
	private UserInStorageUserPickerProvider userPickerProvider;
	private UserStorageSpy userStorageSpy;
	private String guestUserId = "someGuestUserId";

	@BeforeMethod
	public void beforeMethod() {
		userPickerProvider = new UserInStorageUserPickerProvider();
		userStorageSpy = new UserStorageSpy();
		userPickerProvider.startUsingUserStorageAndGuestUserId(userStorageSpy, guestUserId);
	}

	@Test
	public void testPreferenceLevel() throws Exception {
		assertEquals(userPickerProvider.getOrderToSelectImplementionsBy(), 0);
	}

	@Test
	public void testDefaultNoArgsConstructorAsUsedByServiceLoaderExists() throws Exception {
		assertTrue(userPickerProvider instanceof UserPickerProvider);
	}

	@Test
	public void testGetUserPicker() throws Exception {
		UserInStorageUserPicker userPicker = (UserInStorageUserPicker) userPickerProvider
				.getUserPicker();
		assertTrue(userPicker instanceof UserInStorageUserPicker);
		assertEquals(userPicker.getCurrentGuestUserId(), guestUserId);
		assertEquals(userPicker.getUserStorage(), userStorageSpy);
	}

	@Test
	public void testGetUserPickerReturnsUniqeForEachCall() throws Exception {
		UserInStorageUserPicker userPicker = (UserInStorageUserPicker) userPickerProvider
				.getUserPicker();
		UserInStorageUserPicker userPicker2 = (UserInStorageUserPicker) userPickerProvider
				.getUserPicker();
		assertNotEquals(userPicker, userPicker2);
	}

	@Test
	public void testReturnedUserPickersUsesSameStorageAndGuestId() throws Exception {
		UserInStorageUserPicker userPicker = (UserInStorageUserPicker) userPickerProvider
				.getUserPicker();
		UserInStorageUserPicker userPicker2 = (UserInStorageUserPicker) userPickerProvider
				.getUserPicker();
		assertSame(userPicker.getUserStorage(), userPicker2.getUserStorage());
		assertSame(userPicker.getCurrentGuestUserId(), userPicker2.getCurrentGuestUserId());
	}
}
