package samples;

import orm.Column;
import orm.Setter;
import orm.Table;

@Table(name = "SampleOne")
public class SampleOne {
	private String name;
	private boolean isMale;
	private double height;
	private int pk;

	public SampleOne() {

	}

	@Column(name = "name", title = "person's name", type = "String")
	public String getName() {
		return name;
	}

	@Column(name = "isMale", title = "gender", type = "boolean")
	public boolean isMale() {
		return isMale;
	}

	@Column(name = "height", title = "person's height", type = "decimal")
	public double getHeight() {
		return height;
	}

	@Column(name = "pk", isKey = true, type = "Integer")
	public int getPk() {
		return pk;
	}

	@Setter(fieldName = "name")
	public void setName(String name) {
		this.name = name;
	}

	@Setter(fieldName = "isMale")
	public void setMale(boolean isMale) {
		this.isMale = isMale;
	}

	@Setter(fieldName = "height")
	public void setHeight(double height) {
		this.height = height;
	}

	@Setter(fieldName = "pk")
	public void setPk(int pk) {
		this.pk = pk;
	}

}
