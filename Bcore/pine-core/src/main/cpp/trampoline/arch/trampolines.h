//
// Created by canyie on 2020/3/21.
//

#ifndef PINE_TRAMPOLINES_H
#define PINE_TRAMPOLINES_H

extern "C" {

#ifndef __i386__
#ifdef __arm__
void pine_thumb_direct_jump_trampoline();
void pine_thumb_direct_jump_trampoline_jump_entry();

void pine_thumb_bridge_jump_trampoline();
void pine_thumb_bridge_jump_trampoline_target_method();
void pine_thumb_bridge_jump_trampoline_extras();
void pine_thumb_bridge_jump_trampoline_bridge_method();
void pine_thumb_bridge_jump_trampoline_bridge_entry();
void pine_thumb_bridge_jump_trampoline_call_origin_entry();

void pine_thumb_call_origin_trampoline();
void pine_thumb_call_origin_trampoline_origin_method();
void pine_thumb_call_origin_trampoline_origin_code_entry();

void pine_thumb_backup_trampoline();
void pine_thumb_backup_trampoline_origin_method();
void pine_thumb_backup_trampoline_override_space();
void pine_thumb_backup_trampoline_remaining_code_entry();

void pine_thumb_trampolines_end();
#else

void pine_bridge_jump_trampoline();
void pine_bridge_jump_trampoline_target_method();
void pine_bridge_jump_trampoline_extras();
void pine_bridge_jump_trampoline_bridge_method();
void pine_bridge_jump_trampoline_bridge_entry();
void pine_bridge_jump_trampoline_call_origin_entry();

void pine_direct_jump_trampoline();
void pine_direct_jump_trampoline_jump_entry();

void pine_call_origin_trampoline();
void pine_call_origin_trampoline_origin_method();
void pine_call_origin_trampoline_origin_code_entry();

void pine_backup_trampoline();
void pine_backup_trampoline_origin_method();
void pine_backup_trampoline_override_space();
void pine_backup_trampoline_remaining_code_entry();

void pine_trampolines_end();
#endif
#endif
};

#endif //PINE_TRAMPOLINES_H
