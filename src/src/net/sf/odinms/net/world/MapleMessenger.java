package net.sf.odinms.net.world;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MapleMessenger implements Serializable {
	private static final long serialVersionUID = 9179541993413738569L;
	private List<MapleMessengerCharacter> members = new LinkedList<MapleMessengerCharacter>();
	private int id;
	private boolean[] pos = new boolean[] { false, false, false };
	@SuppressWarnings("unused")

	public MapleMessenger(int id, MapleMessengerCharacter chrfor) {
		this.id = id;
		addMember(chrfor);
	}

	public boolean containsMembers(MapleMessengerCharacter member) {
		return members.contains(member);
	}

	public void addMember(MapleMessengerCharacter member) {
		members.add(member);
		int position = getLowestPosition();
		member.setPosition(position);
		pos[position] = true;
	}

	public void removeMember(MapleMessengerCharacter member) {
		pos[member.getPosition()] = false;
		members.remove(member);
	}

	public void silentRemoveMember(MapleMessengerCharacter member) {
		members.remove(member);
	}

	public void silentAddMember(MapleMessengerCharacter member, int position) {
		members.add(member);
		member.setPosition(position);
	}

	public void updateMember(MapleMessengerCharacter member) {
		for (int i = 0; i < members.size(); i++) {
			MapleMessengerCharacter chr = members.get(i);
			if (chr.equals(member)) {
				members.set(i, member);
			}
		}
	}

	public Collection<MapleMessengerCharacter> getMembers() {
		return Collections.unmodifiableList(members);
	}

	public int getLowestPosition() {
		for (int i = 0; i < 3; i++) {
			if (!pos[i]) {
				return i;
			}
		}
		return -1;
	}

	public int getPositionByName(String name) {
		for (MapleMessengerCharacter messengerchar : members) {
			if (messengerchar.getName().equals(name)) {
				return messengerchar.getPosition();
			}
		}
		return -1;
	}

	public void removeMemberByName(String name) {
		for (MapleMessengerCharacter chr : members) {
			if (chr.getName().equals(name)) {
				removeMember(chr);
			}
		}
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final MapleMessenger other = (MapleMessenger) obj;
		if (id != other.id)
			return false;
		return true;
	}
}