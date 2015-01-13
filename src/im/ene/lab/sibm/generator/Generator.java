package im.ene.lab.sibm.generator;

import java.rmi.server.UID;
import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;

import im.ene.lab.sibm.models.NPerson;
import im.ene.lab.sibm.models.NUserType;
import im.ene.lab.sibm.models.Profile;

import com.hp.hpl.jena.shared.uuid.UUID_V4;
import com.hp.hpl.jena.shared.uuid.UUID_V4_Gen;

public class Generator {

	private static int DEFAULT_MIN_AGE = 3;

	private static int DEFAULT_MAX_AGE = 90;

	private static String[] GENDER = { "Male", "Female" };

	public static void main(String[] args) {

		int t1 = 0, t2 = 0, t3 = 0;
		for (int i = 0; i < 5000; i++) {
			NPerson p = genPerson(genRandomInt(30, 60));

			if (NUserType.EVACUEE.equals(p.getType())) {
				t1++;
			} else if (NUserType.VOLUNTEER.equals(p.getType())) {
				t2++;
			} else if (NUserType.ASSISTANT.equals(p.getType())) {
				t3++;
			}

		}

		System.out.println(t1 + " | " + t2 + " | " + t3);

	}

	private static NPerson genPerson(int age, boolean isDead) {
		NPerson p = genPerson(age);
		if (isDead)
			p.setProfile(null);

		return p;
	}

	private static NPerson genPersion(int age, String fName, boolean isDead) {
		NPerson p = genPerson(age, fName);
		if (isDead)
			p.setProfile(null);

		return p;
	}

	private static NPerson genPerson(int age, String fName) {
		NPerson p = genPerson(age);
		p.getProfile().setSurname(genLastName(fName));

		return p;
	}

	private static NPerson genPerson(int age) {
		Profile profile = new Profile();
		profile.setAge(Integer.toString(age));
		profile.setBirthday(genBirthDate(age));
		profile.setFirstName(genFirstName());
		profile.setSurname(genLastName());
		profile.setEmail(genEmail(profile.getFirstName(), profile.getSurname()));
		profile.setGender(genGender(false));
		profile.setPhone(genPhone("+81"));

		UUID id = UUID.randomUUID();
		profile.setUserID(id.toString());

		NPerson person = new NPerson();
		person.setProfile(profile);
		person.setType(genType(age));

		return person;
	}

	private static NUserType genType(int age) {
		final int ran = (new Random()).nextInt(100);

		if (age < 16 || age > 60) {
			// cannot be volunteer or assistant
			return NUserType.EVACUEE;
		} else if (age < 27) {
			// volunteer or evacuee;
			if (ran >= 15) {
				return NUserType.EVACUEE;
			} else
				return NUserType.VOLUNTEER;
		} else {
			if (ran >= 20) {
				return NUserType.EVACUEE;
			} else if (ran >= 5) {
				return NUserType.VOLUNTEER;
			} else {
				return NUserType.ASSISTANT;
			}
		}
	}

	public static NPerson genPerson() {
		int age = genRandomInt(DEFAULT_MIN_AGE, DEFAULT_MAX_AGE);
		return genPerson(age);
	}

	private static String genBirthDate(int age) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -age);

		int year = calendar.get(Calendar.YEAR);
		int month = genMonth();
		int date = genDateByMonth(month);

		return year + "-" + month + "-" + date;
	}

	private static String genGender(boolean isMale) {
		if (!isMale)
			return GENDER[genRandomInt(0, 1)];
		return "Male";
	}

	private static int genMonth() {
		return genRandomInt(1, 12);
	}

	private static int genDateByMonth(int month) {
		int max = 31;
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			max = 31;
			break;
		case 2:
			max = 28;
		case 4:
		case 6:
		case 9:
		case 11:
			max = 30;
			break;
		default:
			max = -1;
			break;
		}

		if (max == -1)
			return 1;

		return genRandomInt(1, max);
	}

	private static String genFirstName() {
		return fixName(RandomStringUtils.randomAlphabetic(genRandomInt(3, 5)));
	}

	private static String genEmail(String firstName, String lastName) {
		return firstName + "_" + lastName + "@"
				+ RandomStringUtils.randomAlphabetic(genRandomInt(3, 5)) + "."
				+ "com";
	}

	private static String genPhone(String seed) {
		return seed + "xx-xxxx-xxxx";
	}

	private static String genLastName() {
		return fixName(genLastName(null));
	}

	private static String genLastName(String seed) {
		if (seed == null || seed.length() == 0)
			return RandomStringUtils.randomAlphabetic(genRandomInt(2, 5));

		return seed;
	}

	public static int genRandomInt(int min, int max) {
		if (max <= min)
			return min;

		int result = min + (int) (Math.random() * (max - min + 1));
		return result;
	}

	private static String fixName(String original) {
		return original.length() == 0 ? original : original.substring(0, 1)
				.toUpperCase() + original.substring(1);
	}

	public static NPerson[] genFamily(String fName, int depth) {
		// depth = 0: single;
		// depth = 1: parent - children
		// depth = 2: grandparent - parent - children
		// max depth = 2;
		
		if (depth > 2) depth = 2;
		if (depth < 0) depth = 0;
		
		NPerson grandFather = new NPerson();
		NPerson grandMother = new NPerson();

		NPerson father = new NPerson();
		NPerson mother = new NPerson();

		NPerson[] children = null;

		int childAverageAge = genRandomInt(6, 25);

		int fatherAge, motherAge, grandFatherAge, grandMotherAge;

		int[] childAge = null;

		if (depth == 0) {
			return new NPerson[] { genPerson() };
		} else {
			int childCount = genRandomInt(0, 3);
			if (childCount == 0) {
				// start from father age
				fatherAge = genRandomInt(20, 60);
				motherAge = fatherAge + genRandomInt(-4, 4);

				grandFatherAge = fatherAge + genRandomInt(20, 30);
				grandMotherAge = grandFatherAge + genRandomInt(-4, 4);
			} else {
				fatherAge = childAverageAge + genRandomInt(20, 30);
				motherAge = childAverageAge + genRandomInt(-4, 4);

				grandFatherAge = fatherAge + genRandomInt(20, 30);
				grandMotherAge = grandFatherAge + genRandomInt(-4, 4);

				childAge = new int[childCount];
				for (int i = 0; i < childAge.length; i++)
					childAge[i] = childAverageAge + genRandomInt(-3, 3);
			}

			boolean hasChild = false;
			// generate
			if (childAge != null && childAge.length > 0) {
				hasChild = true;
				children = new NPerson[childAge.length];
				for (int i = 0; i < childAge.length; i++) {
					children[i] = genPerson(childAge[i], fName);
				}
			} else {
				hasChild = false;
				children = new NPerson[0];
			}

			father = genPerson(fatherAge, fName);
			father.getProfile().setGender(GENDER[0]);

			mother = genPerson(motherAge, fName);
			mother.getProfile().setGender(GENDER[1]);

			father.setSpouse(mother);
			mother.setSpouse(father);

			if (hasChild) {
				father.setChildren(children);
				mother.setChildren(children);
			}

			NPerson[] parent = new NPerson[] { father, mother };
			NPerson[] familyAtDepth1 = ArrayUtils.addAll(parent, children);

			if (depth == 1) {
				// return is ok
				return familyAtDepth1;
			} else {
				// continue
				grandFather = genPersion(grandFatherAge, fName,
						(Math.random() > 0.5));
				if (grandFather.getProfile() != null)
					grandFather.getProfile().setGender(GENDER[0]);

				grandMother = genPersion(grandMotherAge, fName,
						(Math.random() > 0.5));
				if (grandMother.getProfile() != null)
					grandMother.getProfile().setGender(GENDER[1]);

				grandFather.setSpouse(grandMother);
				grandMother.setSpouse(grandFather);

				// FIXME
				grandFather.setChildren(parent);
				grandMother.setChildren(parent);

				father.setFather(grandFather);
				father.setMother(grandMother);

				mother.setFather(grandFather);
				mother.setMother(grandMother);

				boolean hasGrandFather = grandFather.getProfile() != null;
				boolean hasGrandMother = grandMother.getProfile() != null;

				NPerson[] familyAtDepth2 = familyAtDepth1;

				if (hasGrandFather) {
					familyAtDepth2 = ArrayUtils
							.add(familyAtDepth2, grandFather);
				}

				if (hasGrandMother) {
					familyAtDepth2 = ArrayUtils
							.add(familyAtDepth2, grandMother);
				}

				return familyAtDepth2;
			}
		}
	}
}
