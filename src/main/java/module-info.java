module se.uu.ub.cora.userpicker {
	requires transitive se.uu.ub.cora.gatekeeper;
	requires se.uu.ub.cora.logger;
	requires se.uu.ub.cora.initialize;

	exports se.uu.ub.cora.userpicker;

	provides se.uu.ub.cora.gatekeeper.picker.UserPickerInstanceProvider
			with se.uu.ub.cora.userpicker.UserInStorageUserPickerProvider;
}