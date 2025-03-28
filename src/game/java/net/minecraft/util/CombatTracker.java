package net.minecraft.util;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**+
 * This portion of EaglercraftX contains deobfuscated Minecraft 1.8 source code.
 * 
 * Minecraft 1.8.8 bytecode is (c) 2015 Mojang AB. "Do not distribute!"
 * Mod Coder Pack v9.18 deobfuscation configs are (c) Copyright by the MCP Team
 * 
 * EaglercraftX 1.8 patch files (c) 2022-2025 lax1dude, ayunami2000. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
public class CombatTracker {
	/**+
	 * The CombatEntry objects that we've tracked so far.
	 */
	private final List<CombatEntry> combatEntries = Lists.newArrayList();
	private final EntityLivingBase fighter;
	private int field_94555_c;
	private int field_152775_d;
	private int field_152776_e;
	private boolean field_94552_d;
	private boolean field_94553_e;
	private String field_94551_f;

	public CombatTracker(EntityLivingBase fighterIn) {
		this.fighter = fighterIn;
	}

	public void func_94545_a() {
		this.func_94542_g();
		if (this.fighter.isOnLadder()) {
			Block block = this.fighter.worldObj.getBlockState(
					new BlockPos(this.fighter.posX, this.fighter.getEntityBoundingBox().minY, this.fighter.posZ))
					.getBlock();
			if (block == Blocks.ladder) {
				this.field_94551_f = "ladder";
			} else if (block == Blocks.vine) {
				this.field_94551_f = "vines";
			}
		} else if (this.fighter.isInWater()) {
			this.field_94551_f = "water";
		}

	}

	/**+
	 * Adds an entry for the combat tracker
	 */
	public void trackDamage(DamageSource damageSrc, float healthIn, float damageAmount) {
		this.reset();
		this.func_94545_a();
		CombatEntry combatentry = new CombatEntry(damageSrc, this.fighter.ticksExisted, healthIn, damageAmount,
				this.field_94551_f, this.fighter.fallDistance);
		this.combatEntries.add(combatentry);
		this.field_94555_c = this.fighter.ticksExisted;
		this.field_94553_e = true;
		if (combatentry.isLivingDamageSrc() && !this.field_94552_d && this.fighter.isEntityAlive()) {
			this.field_94552_d = true;
			this.field_152775_d = this.fighter.ticksExisted;
			this.field_152776_e = this.field_152775_d;
			this.fighter.sendEnterCombat();
		}

	}

	public IChatComponent getDeathMessage() {
		if (this.combatEntries.size() == 0) {
			return new ChatComponentTranslation("death.attack.generic", new Object[] { this.fighter.getDisplayName() });
		} else {
			CombatEntry combatentry = this.func_94544_f();
			CombatEntry combatentry1 = (CombatEntry) this.combatEntries.get(this.combatEntries.size() - 1);
			IChatComponent ichatcomponent = combatentry1.getDamageSrcDisplayName();
			Entity entity = combatentry1.getDamageSrc().getEntity();
			Object object;
			if (combatentry != null && combatentry1.getDamageSrc() == DamageSource.fall) {
				IChatComponent ichatcomponent1 = combatentry.getDamageSrcDisplayName();
				if (combatentry.getDamageSrc() != DamageSource.fall
						&& combatentry.getDamageSrc() != DamageSource.outOfWorld) {
					if (ichatcomponent1 != null
							&& (ichatcomponent == null || !ichatcomponent1.equals(ichatcomponent))) {
						Entity entity1 = combatentry.getDamageSrc().getEntity();
						ItemStack itemstack1 = entity1 instanceof EntityLivingBase
								? ((EntityLivingBase) entity1).getHeldItem()
								: null;
						if (itemstack1 != null && itemstack1.hasDisplayName()) {
							object = new ChatComponentTranslation("death.fell.assist.item", new Object[] {
									this.fighter.getDisplayName(), ichatcomponent1, itemstack1.getChatComponent() });
						} else {
							object = new ChatComponentTranslation("death.fell.assist",
									new Object[] { this.fighter.getDisplayName(), ichatcomponent1 });
						}
					} else if (ichatcomponent != null) {
						ItemStack itemstack = entity instanceof EntityLivingBase
								? ((EntityLivingBase) entity).getHeldItem()
								: null;
						if (itemstack != null && itemstack.hasDisplayName()) {
							object = new ChatComponentTranslation("death.fell.finish.item", new Object[] {
									this.fighter.getDisplayName(), ichatcomponent, itemstack.getChatComponent() });
						} else {
							object = new ChatComponentTranslation("death.fell.finish",
									new Object[] { this.fighter.getDisplayName(), ichatcomponent });
						}
					} else {
						object = new ChatComponentTranslation("death.fell.killer",
								new Object[] { this.fighter.getDisplayName() });
					}
				} else {
					object = new ChatComponentTranslation("death.fell.accident." + this.func_94548_b(combatentry),
							new Object[] { this.fighter.getDisplayName() });
				}
			} else {
				object = combatentry1.getDamageSrc().getDeathMessage(this.fighter);
			}

			return (IChatComponent) object;
		}
	}

	public EntityLivingBase func_94550_c() {
		EntityLivingBase entitylivingbase = null;
		EntityPlayer entityplayer = null;
		float f = 0.0F;
		float f1 = 0.0F;

		for (int i = 0, l = this.combatEntries.size(); i < l; ++i) {
			CombatEntry combatentry = this.combatEntries.get(i);
			if (combatentry.getDamageSrc().getEntity() instanceof EntityPlayer
					&& (entityplayer == null || combatentry.func_94563_c() > f1)) {
				f1 = combatentry.func_94563_c();
				entityplayer = (EntityPlayer) combatentry.getDamageSrc().getEntity();
			}

			if (combatentry.getDamageSrc().getEntity() instanceof EntityLivingBase
					&& (entitylivingbase == null || combatentry.func_94563_c() > f)) {
				f = combatentry.func_94563_c();
				entitylivingbase = (EntityLivingBase) combatentry.getDamageSrc().getEntity();
			}
		}

		if (entityplayer != null && f1 >= f / 3.0F) {
			return entityplayer;
		} else {
			return entitylivingbase;
		}
	}

	private CombatEntry func_94544_f() {
		CombatEntry combatentry = null;
		CombatEntry combatentry1 = null;
		byte b0 = 0;
		float f = 0.0F;

		for (int i = 0; i < this.combatEntries.size(); ++i) {
			CombatEntry combatentry2 = (CombatEntry) this.combatEntries.get(i);
			CombatEntry combatentry3 = i > 0 ? (CombatEntry) this.combatEntries.get(i - 1) : null;
			if ((combatentry2.getDamageSrc() == DamageSource.fall
					|| combatentry2.getDamageSrc() == DamageSource.outOfWorld) && combatentry2.getDamageAmount() > 0.0F
					&& (combatentry == null || combatentry2.getDamageAmount() > f)) {
				if (i > 0) {
					combatentry = combatentry3;
				} else {
					combatentry = combatentry2;
				}

				f = combatentry2.getDamageAmount();
			}

			if (combatentry2.func_94562_g() != null
					&& (combatentry1 == null || combatentry2.func_94563_c() > (float) b0)) {
				combatentry1 = combatentry2;
			}
		}

		if (f > 5.0F && combatentry != null) {
			return combatentry;
		} else if (b0 > 5 && combatentry1 != null) {
			return combatentry1;
		} else {
			return null;
		}
	}

	private String func_94548_b(CombatEntry parCombatEntry) {
		return parCombatEntry.func_94562_g() == null ? "generic" : parCombatEntry.func_94562_g();
	}

	public int func_180134_f() {
		return this.field_94552_d ? this.fighter.ticksExisted - this.field_152775_d
				: this.field_152776_e - this.field_152775_d;
	}

	private void func_94542_g() {
		this.field_94551_f = null;
	}

	/**+
	 * Resets this trackers list of combat entries
	 */
	public void reset() {
		int i = this.field_94552_d ? 300 : 100;
		if (this.field_94553_e
				&& (!this.fighter.isEntityAlive() || this.fighter.ticksExisted - this.field_94555_c > i)) {
			boolean flag = this.field_94552_d;
			this.field_94553_e = false;
			this.field_94552_d = false;
			this.field_152776_e = this.fighter.ticksExisted;
			if (flag) {
				this.fighter.sendEndCombat();
			}

			this.combatEntries.clear();
		}

	}

	/**+
	 * Returns EntityLivingBase assigned for this CombatTracker
	 */
	public EntityLivingBase getFighter() {
		return this.fighter;
	}
}