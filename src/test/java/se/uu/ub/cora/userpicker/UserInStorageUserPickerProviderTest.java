/*
 * Copyright 2019, 2022 Olov McKie
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
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.gatekeeper.picker.UserPickerInstanceProvider;
import se.uu.ub.cora.gatekeeper.storage.UserStorageProvider;
import se.uu.ub.cora.gatekeeper.storage.UserStorageView;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;

public class UserInStorageUserPickerProviderTest {
	private UserInStorageUserPickerProvider userPickerProvider;
	private String guestUserId = "someGuestUserId";
	private UserStorageViewInstanceProviderSpy userStorageInstanceProvider;
	private MapSpy<String, String> settingsMapSpy;

	@BeforeMethod
	public void beforeMethod() {
		LoggerFactorySpy loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);
		userStorageInstanceProvider = new UserStorageViewInstanceProviderSpy();
		UserStorageProvider
				.onlyForTestSetUserStorageViewInstanceProvider(userStorageInstanceProvider);

		settingsMapSpy = new MapSpy<>();
		settingsMapSpy.put("guestUserId", guestUserId);
		SettingsProvider.setSettings(settingsMapSpy);

		userPickerProvider = new UserInStorageUserPickerProvider();
	}

	@Test
	public void testPreferenceLevel() throws Exception {
		assertEquals(userPickerProvider.getOrderToSelectImplementionsBy(), 0);
	}

	@Test
	public void testDefaultNoArgsConstructorAsUsedByServiceLoaderExists() throws Exception {
		assertTrue(userPickerProvider instanceof UserPickerInstanceProvider);
	}

	@Test
	public void testGetUserPicker() throws Exception {
		UserInStorageUserPicker userPicker = (UserInStorageUserPicker) userPickerProvider
				.getUserPicker();

		assertTrue(userPicker instanceof UserInStorageUserPicker);
		assertEquals(userPicker.onlyForTestGetCurrentGuestUserId(), guestUserId);
		UserStorageView userStorage = userPicker.onlyForTestGetUserStorage();

		userStorageInstanceProvider.MCR.assertNumberOfCallsToMethod("getStorageView", 1);
		userStorageInstanceProvider.MCR.assertReturn("getStorageView", 0, userStorage);
	}

	@Test
	public void testGetUserPickerReturnsUniqeForEachCall() throws Exception {
		UserInStorageUserPicker userPicker = (UserInStorageUserPicker) userPickerProvider
				.getUserPicker();
		UserInStorageUserPicker userPicker2 = (UserInStorageUserPicker) userPickerProvider
				.getUserPicker();

		assertNotEquals(userPicker, userPicker2);

		userStorageInstanceProvider.MCR.assertNumberOfCallsToMethod("getStorageView", 2);

		UserStorageView userStorage = userPicker.onlyForTestGetUserStorage();
		userStorageInstanceProvider.MCR.assertReturn("getStorageView", 0, userStorage);

		UserStorageView userStorage2 = userPicker2.onlyForTestGetUserStorage();
		userStorageInstanceProvider.MCR.assertReturn("getStorageView", 1, userStorage2);

		settingsMapSpy.MCR.assertNumberOfCallsToMethod("get", 1);
	}
}
