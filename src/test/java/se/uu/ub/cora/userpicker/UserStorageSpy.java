/*
 * Copyright 2017 Uppsala University Library
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

import se.uu.ub.cora.bookkeeper.data.DataAtomic;
import se.uu.ub.cora.bookkeeper.data.DataGroup;

public class UserStorageSpy implements UserStorage {
	public boolean getGuestUserIsCalled = false;
	public boolean getUserByIdIsCalled = false;
	boolean guestIsActive = true;

	@Override
	public DataGroup getGuestUser() {
		getGuestUserIsCalled = true;
		if (guestIsActive) {
			return createUserWithRecordIdAndRoleNames("12345", true, "guest");
		}
		return createUserWithRecordIdAndRoleNames("12345", false, "guest");

	}

	public void setGuestToInactive() {
		guestIsActive = false;
	}

	private static DataGroup createUserWithRecordIdAndRoleNames(String userRecordId,
			boolean activeStatus, String... roleNames) {
		DataGroup recordInfo = createRecordInfoWithRecordTypeAndRecordId("user", userRecordId);
		DataGroup userDataGroup = DataGroup.withNameInData("user");
		userDataGroup.addChild(recordInfo);
		addRoleNamesAsRoles(userDataGroup, roleNames);
		if (activeStatus) {
			userDataGroup.addChild(DataAtomic.withNameInDataAndValue("activeStatus", "active"));
		} else {
			userDataGroup.addChild(DataAtomic.withNameInDataAndValue("activeStatus", "inactive"));
		}
		return userDataGroup;
	}

	public static DataGroup createRecordInfoWithRecordTypeAndRecordId(String recordType,
			String recordId) {
		DataGroup recordInfo = DataGroup.withNameInData("recordInfo");
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("type", recordType));
		recordInfo.addChild(DataAtomic.withNameInDataAndValue("id", recordId));
		return recordInfo;
	}

	private static void addRoleNamesAsRoles(DataGroup user, String... roleNames) {
		for (String roleName : roleNames) {
			DataGroup userRole = DataGroup.withNameInData("userRole");
			user.addChild(userRole);
			userRole.addChild(createPermissionRoleWithPermissionRoleId(roleName));
		}
	}

	private static DataGroup createPermissionRoleWithPermissionRoleId(String roleName) {
		DataGroup userRoleLink = DataGroup.withNameInData("userRole");
		userRoleLink
				.addChild(DataAtomic.withNameInDataAndValue("linkedRecordType", "permissionRole"));
		userRoleLink.addChild(DataAtomic.withNameInDataAndValue("linkedRecordId", roleName));
		return userRoleLink;
	}

	@Override
	public DataGroup getUserById(String id) {
		getUserByIdIsCalled = true;
		if ("unknownUser".equals(id)) {
			throw new RuntimeException("user not found");
		}
		if ("666666".equals(id)) {
			return createUserWithRecordIdAndRoleNames("666666", false, "fitnesse", "metadataAdmin");
		}
		return createUserWithRecordIdAndRoleNames("121212", true, "fitnesse", "metadataAdmin");
	}

}
