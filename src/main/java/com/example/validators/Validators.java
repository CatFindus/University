package com.example.validators;

import com.example.exeptions.IncorrectRequestException;

public abstract class Validators {
    private Validators next;

    public void validate() throws IncorrectRequestException {
        validation();
        if (next!=null) next.validation();
    }

    protected abstract void validation() throws IncorrectRequestException;

    public void then(Validators validators) {
        if (next!=null) next.then(validators);
        else next = validators;
    }
}
