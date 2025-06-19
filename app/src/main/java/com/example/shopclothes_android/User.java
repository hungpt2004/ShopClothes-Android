package com.example.shopclothes_android;

public class User {
			private String name;
			private String email;
			private String phone;
			private String birthDate;
			private String gender;
			private String avatarPath;

			public User() {
						// Default constructor
			}

			public User(String name, String email, String phone, String birthDate, String gender, String address) {
						this.name = name;
						this.email = email;
						this.phone = phone;
						this.birthDate = birthDate;
						this.gender = gender;
			}

			// Getters with null safety
			public String getName() {
						return name != null ? name : "";
			}

			public String getEmail() {
						return email != null ? email : "";
			}

			public String getPhone() {
						return phone != null ? phone : "";
			}

			public String getBirthDate() {
						return birthDate != null ? birthDate : "";
			}

			public String getGender() {
						return gender != null ? gender : "";
			}



			public String getAvatarPath() {
						return avatarPath != null ? avatarPath : "";
			}

			// Setters
			public void setName(String name) {
						this.name = name;
			}

			public void setEmail(String email) {
						this.email = email;
			}

			public void setPhone(String phone) {
						this.phone = phone;
			}

			public void setBirthDate(String birthDate) {
						this.birthDate = birthDate;
			}

			public void setGender(String gender) {
						this.gender = gender;
			}



			public void setAvatarPath(String avatarPath) {
						this.avatarPath = avatarPath;
			}

			// Utility methods
			public boolean hasAvatar() {
						return avatarPath != null && !avatarPath.isEmpty();
			}

			public String getDisplayName() {
						return name != null && !name.isEmpty() ? name : "User";
			}

			public boolean isProfileComplete() {
						return name != null && !name.isEmpty() &&
														email != null && !email.isEmpty() &&
														phone != null && !phone.isEmpty();
			}

			@Override
			public String toString() {
						return "User{" +
														"name='" + name + '\'' +
														", email='" + email + '\'' +
														", phone='" + phone + '\'' +
														", birthDate='" + birthDate + '\'' +
														", gender='" + gender + '\'' +
														", avatarPath='" + avatarPath + '\'' +
														'}';
			}
}
