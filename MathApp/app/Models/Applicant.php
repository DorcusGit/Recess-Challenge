<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Applicant extends Model
{
    use HasFactory;

    protected $table = 'applicants'; // specify the table name

    protected $primaryKey = 'applicantID'; // specify the primary key

    protected $fillable = [
        'schoolRegNo',
        'emailAddress',
        'userName',
        'imagePath',
        'firstName',
        'lastName',
        'password',
        'dateOfBirth',
    ];

    protected $hidden = [
        'password',
    ];

    public function school()
    {
        return $this->belongsTo(School::class, 'schoolRegNo', 'schoolRegNo');
    }

    // Define other relationships or additional methods if needed
}

