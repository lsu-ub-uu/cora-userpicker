/*
 * Copyright 2022 Uppsala University Library
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

import java.util.function.Supplier;

import se.uu.ub.cora.gatekeeper.storage.UserStorageView;
import se.uu.ub.cora.gatekeeper.user.AppToken;
import se.uu.ub.cora.gatekeeper.user.User;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class UserStorageViewSpy implements UserStorageView {
	private static final String GUEST_ID = "12345";
	public boolean getGuestUserIsCalled = false;
	public boolean getUserByIdIsCalled = false;
	boolean guestIsActive = true;
	public boolean getUserByLoginIsCalled = false;
	public String lastCalledId;

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public UserStorageViewSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("getUserById", (Supplier<User>) () -> createUser());
		MRV.setDefaultReturnValuesSupplier("getUserByLoginId", (Supplier<User>) () -> createUser());
		MRV.setDefaultReturnValuesSupplier("getAppTokenById",
				(Supplier<AppToken>) () -> createApptoken());
	}

	private User createUser() {
		User user = new User("someId");
		user.active = true;
		return user;
	}

	private AppToken createApptoken() {
		return new AppToken("someId", "token");
	}

	@Override
	public User getUserById(String userId) {
		return (User) MCR.addCallAndReturnFromMRV("userId", userId);
	}

	@Override
	public User getUserByLoginId(String loginId) {
		return (User) MCR.addCallAndReturnFromMRV("loginId", loginId);
	}

	@Override
	public AppToken getAppTokenById(String tokenId) {
		return (AppToken) MCR.addCallAndReturnFromMRV("tokenId", tokenId);
	}

	@Override
	public String getSystemSecretById(String systemSecretId) {
		// TODO Auto-generated method stub
		return null;
	}

}
