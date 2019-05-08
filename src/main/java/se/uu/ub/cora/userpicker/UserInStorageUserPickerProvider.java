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

import se.uu.ub.cora.gatekeeper.user.UserPicker;
import se.uu.ub.cora.gatekeeper.user.UserPickerProvider;
import se.uu.ub.cora.gatekeeper.user.UserStorage;

public class UserInStorageUserPickerProvider implements UserPickerProvider {

	private UserStorage userStorage;
	private String guestUserId;

	@Override
	public UserPicker getUserPicker() {
		return UserInStorageUserPicker.usingUserStorageAndGuestUserId(userStorage, guestUserId);
	}

	@Override
	public void startUsingUserStorageAndGuestUserId(UserStorage userStorage, String guestUserId) {
		this.userStorage = userStorage;
		this.guestUserId = guestUserId;
	}

	@Override
	public int getOrderToSelectImplementionsBy() {
		return 0;
	}

}
