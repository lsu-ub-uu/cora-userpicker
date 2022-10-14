/*
 * Copyright 2017, 2018, 2022 Uppsala University Library
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

import se.uu.ub.cora.gatekeeper.user.AppToken;
import se.uu.ub.cora.gatekeeper.user.User;
import se.uu.ub.cora.gatekeeper.user.UserStorageView;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class UserStorageViewSpy implements UserStorageView {
	private static final String GUEST_ID = "12345";
	public boolean getGuestUserIsCalled = false;
	public boolean getUserByIdIsCalled = false;
	boolean guestIsActive = true;
	public boolean getUserByIdFromLoginIsCalled = false;
	public String lastCalledId;

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public UserStorageViewSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("getUserById", (Supplier<User>) () -> createUser());
		MRV.setDefaultReturnValuesSupplier("getUserByIdFromLogin",
				(Supplier<User>) () -> createUser());
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
	public User getUserByIdFromLogin(String idFromLogin) {
		return (User) MCR.addCallAndReturnFromMRV("idFromLogin", idFromLogin);
	}

	@Override
	public AppToken getAppTokenById(String tokenId) {
		return (AppToken) MCR.addCallAndReturnFromMRV("tokenId", tokenId);
	}
	// public void setGuestToInactive() {
	// guestIsActive = false;
	// }
	//
	// private static DataGroup createUserWithRecordIdAndRoleNames(String userRecordId,
	// boolean activeStatus, String... roleNames) {
	// DataGroup recordInfo = createRecordInfoWithRecordTypeAndRecordId("user", userRecordId);
	// DataGroup userDataGroup = new DataGroupSpy("user");
	// userDataGroup.addChild(recordInfo);
	// addRoleNamesAsRoles(userDataGroup, roleNames);
	// if (activeStatus) {
	// userDataGroup.addChild(new DataAtomicSpy("activeStatus", "active"));
	// } else {
	// userDataGroup.addChild(new DataAtomicSpy("activeStatus", "inactive"));
	// }
	// return userDataGroup;
	// }
	//
	// public static DataGroup createRecordInfoWithRecordTypeAndRecordId(String recordType,
	// String recordId) {
	// DataGroup recordInfo = new DataGroupSpy("recordInfo");
	// recordInfo.addChild(new DataAtomicSpy("type", recordType));
	// recordInfo.addChild(new DataAtomicSpy("id", recordId));
	// return recordInfo;
	// }
	//
	// private static void addRoleNamesAsRoles(DataGroup user, String... roleNames) {
	// for (String roleName : roleNames) {
	// DataGroup userRole = new DataGroupSpy("userRole");
	// user.addChild(userRole);
	// userRole.addChild(createPermissionRoleWithPermissionRoleId(roleName));
	// }
	// }
	//
	// private static DataGroup createPermissionRoleWithPermissionRoleId(String roleName) {
	// DataGroup userRoleLink = new DataGroupSpy("userRole");
	// userRoleLink.addChild(new DataAtomicSpy("linkedRecordType", "permissionRole"));
	// userRoleLink.addChild(new DataAtomicSpy("linkedRecordId", roleName));
	// return userRoleLink;
	// }
	//
	// @Override
	// public DataGroup getUserById(String id) {
	// this.lastCalledId = id;
	// getUserByIdIsCalled = true;
	// if ("unknownUser".equals(id)) {
	// throw new RuntimeException("user not found");
	// }
	// if ("666666".equals(id)) {
	// return createUserWithRecordIdAndRoleNames("666666", false, "fitnesse", "metadataAdmin");
	// }
	// if ("1111111".equals(id)) {
	// DataGroup userGroup = createUserWithRecordIdAndRoleNames("1111111", true, "namedUser",
	// "metadataAdmin");
	// userGroup.addChild(new DataAtomicSpy("userFirstname", "firstName"));
	// userGroup.addChild(new DataAtomicSpy("userLastname", "lastName"));
	// return userGroup;
	// }
	// if (GUEST_ID.equals(id)) {
	// if (guestIsActive) {
	// return createUserWithRecordIdAndRoleNames(GUEST_ID, true, "guest");
	// }
	// return createUserWithRecordIdAndRoleNames(GUEST_ID, false, "guest");
	// }
	// return createUserWithRecordIdAndRoleNames("121212", true, "fitnesse", "metadataAdmin");
	// }
	//
	// @Override
	// public DataGroup getUserByIdFromLogin(String idFromLogin) {
	// getUserByIdFromLoginIsCalled = true;
	// if ("unknown@ub.uu.se".equals(idFromLogin)) {
	// throw new RuntimeException("user not found");
	// }
	// if ("fitnesse@ub.uu.se".equals(idFromLogin)) {
	// return createUserWithRecordIdAndRoleNames("121212", true, "fitnesse", "metadataAdmin");
	// }
	// return createUserWithRecordIdAndRoleNames("141414", false, "fitnesse", "metadataAdmin");
	// }

}
