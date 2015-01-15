package im.ene.lab.sibm.models;

import im.ene.lab.sibm.util.DataUtil;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Profile {

	private String firstName;
	private String surname;
	private String gender;
	private String birthday;
	private String age;
	private String phone;
	private String address;
	private String zipCode;
	private String email;
	private String occupation;

	/**
	 * 
	 * @return The firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * 
	 * @param firstName
	 *            The firstName
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;

	}

	/**
	 * 
	 * @return The surname
	 */
	public String getSurname() {
		return surname;
	}

	/**
	 * 
	 * @param surname
	 *            The surname
	 */
	public void setSurname(String surname) {
		this.surname = surname;

	}

	/**
	 * 
	 * @return The gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * 
	 * @param gender
	 *            The gender
	 */
	public void setGender(String gender) {
		this.gender = gender;

	}

	/**
	 * 
	 * @return The birthday
	 */
	public String getBirthday() {
		return birthday;
	}

	/**
	 * 
	 * @param birthday
	 *            The birthday
	 */
	public void setBirthday(String birthday) {
		this.birthday = birthday;

	}

	/**
	 * 
	 * @return The age
	 */
	public String getAge() {
		return age;
	}

	/**
	 * 
	 * @param age
	 *            The age
	 */
	public void setAge(String age) {
		this.age = age;

	}

	/**
	 * 
	 * @return The phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * 
	 * @param phone
	 *            The phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;

	}

	/**
	 * 
	 * @return The address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * 
	 * @param address
	 *            The address
	 */
	public void setAddress(String address) {
		this.address = address;

	}

	/**
	 * 
	 * @return The zipCode
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * 
	 * @param zipCode
	 *            The zipCode
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;

	}

	/**
	 * 
	 * @return The email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 
	 * @param email
	 *            The email
	 */
	public void setEmail(String email) {
		this.email = email;

	}

	/**
	 * 
	 * @return The occupation
	 */
	public String getOccupation() {
		return occupation;
	}

	/**
	 * 
	 * @param occupation
	 *            The occupation
	 */
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	
	@Override
	public String toString() {
		return DataUtil.GSON.toJson(this);
	}
}
