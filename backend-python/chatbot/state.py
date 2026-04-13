user_states = {}

def get_user_state(user_id: str):
    if user_id not in user_states:
        user_states[user_id] = {
            "last_products": [],
            "selected_product": None
        }
    return user_states[user_id]


def reset_user_state(user_id: str):
    user_states[user_id] = {
        "last_products": [],
        "selected_product": None
    }